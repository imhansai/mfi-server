package dev.fromnowon.mfiserver.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import dev.fromnowon.mfiserver.config.MfiProperties;
import dev.fromnowon.mfiserver.dto.TokenDataDTO;
import dev.fromnowon.mfiserver.exception.SystemException;
import dev.fromnowon.mfiserver.request.UsedAuthEntitiesRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

/**
 * 根据 ppid 导出 excel
 *
 * @author hansai
 */
@Service
@Validated
@Slf4j
public class MfiService {

    private final AuthEntitiesRequestService authEntitiesRequestService;

    private final FileNameRequestService fileNameRequestService;

    private final FileDownloadService fileDownloadService;

    private final UsedAuthEntitiesService usedAuthEntitiesService;

    private final MfiProperties mfiProperties;

    public MfiService(AuthEntitiesRequestService authEntitiesRequestService,
                      FileNameRequestService fileNameRequestService,
                      FileDownloadService fileDownloadService,
                      UsedAuthEntitiesService usedAuthEntitiesService,
                      MfiProperties mfiProperties) {
        this.authEntitiesRequestService = authEntitiesRequestService;
        this.fileNameRequestService = fileNameRequestService;
        this.fileDownloadService = fileDownloadService;
        this.usedAuthEntitiesService = usedAuthEntitiesService;
        this.mfiProperties = mfiProperties;
    }

    public byte[] getBytes(Integer requestedAuthEntityCount) {
        log.debug("请求获取 {} 个token", requestedAuthEntityCount);
        // Auth Entities Request
        String requestId = authEntitiesRequestService.authEntitiesRequest(requestedAuthEntityCount);
        // File Name Request
        List<String> fileNameList = fileNameRequestService.fileNameRequest(requestId);
        // File Download
        List<Path> filePathList = fileDownloadService.fileDownload(requestId, fileNameList);
        // 获取生成 ppid、Token ID、Base64-encoded Token、CRC32 in HEX、UUID(需要生成)、Product Data
        List<TokenDataDTO> tokenDataDTOList = generateTokenDataDTOList(filePathList);
        // 构造注册方法参数
        UsedAuthEntitiesRequest usedAuthEntitiesRequest = getUsedAuthEntitiesRequest(tokenDataDTOList);
        // Register Used Auth Entity during Factory Provisioning
        usedAuthEntitiesService.usedAuthEntities(usedAuthEntitiesRequest);
        Workbook workbook = getWorkbook(tokenDataDTOList);
        // 将Workbook写入一个ByteArrayOutputStream中
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("把 workbook 写到输出流中异常!" + e.getMessage(), e);
        }
    }

    private UsedAuthEntitiesRequest getUsedAuthEntitiesRequest(List<TokenDataDTO> tokenDataDTOList) {
        UsedAuthEntitiesRequest usedAuthEntitiesRequest = new UsedAuthEntitiesRequest();
        usedAuthEntitiesRequest.setPpid(mfiProperties.getPpid());
        Map<String, String> authEntities = new HashMap<>();
        for (TokenDataDTO tokenDataDTO : tokenDataDTOList) {
            String token = tokenDataDTO.getToken();
            String uuid = tokenDataDTO.getUuid();
            authEntities.put(token, uuid);
        }
        usedAuthEntitiesRequest.setAuthEntities(List.of(authEntities));
        return usedAuthEntitiesRequest;
    }

    public List<TokenDataDTO> generateTokenDataDTOList(List<Path> filePathList) {
        try {
            return getTokenDataDTOList(filePathList);
        } catch (IOException e) {
            throw new SystemException("生成 token 相关数据异常! " + e.getMessage(), e);
        }
    }

    private List<TokenDataDTO> getTokenDataDTOList(List<Path> filePathList) throws IOException {
        List<TokenDataDTO> tokenDataDTOList = new ArrayList<>();
        for (Path filePath : filePathList) {
            File csvFile = filePath.toFile();
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.builder()
                    .addColumn("ppid")
                    .addColumn("token")
                    .addColumn("tokenBase64")
                    .addColumn("tokenHex")
                    .build();
            List<TokenDataDTO> tempTokenDataDTOList;
            try (MappingIterator<TokenDataDTO> schemaMappingIterator = csvMapper.readerFor(TokenDataDTO.class).with(schema).readValues(csvFile)) {
                tempTokenDataDTOList = schemaMappingIterator.readAll();
            }
            tokenDataDTOList.addAll(tempTokenDataDTOList);
        }

        // 为每个 token 分配 uuid 和 productData
        tokenDataDTOList.forEach(tokenDataDTO -> {
            tokenDataDTO.setUuid(UUID.randomUUID().toString());
            tokenDataDTO.setProductData(mfiProperties.getProductData());
        });
        return tokenDataDTOList;
    }

    public Workbook getWorkbook(List<TokenDataDTO> tokenDataDTOList) {
        try {
            return generateWorkbook(tokenDataDTOList);
        } catch (IllegalAccessException e) {
            throw new SystemException("生成 workbook Error" + e.getMessage(), e);
        }
    }

    private Workbook generateWorkbook(List<TokenDataDTO> tokenDataDTOList) throws IllegalAccessException {
        // 为了 Excel 表头
        TokenDataDTO firsttokenDataDTO = new TokenDataDTO();
        firsttokenDataDTO.setPpid("ppid");
        firsttokenDataDTO.setToken("Token ID");
        firsttokenDataDTO.setTokenBase64("Base64-encoded Token");
        firsttokenDataDTO.setTokenHex("CRC32 in HEX");
        firsttokenDataDTO.setUuid("UUID");
        firsttokenDataDTO.setProductData("Product Data");
        tokenDataDTOList.add(0, firsttokenDataDTO);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        for (int i = 0; i < tokenDataDTOList.size(); i++) {
            Row row = sheet.createRow(i);
            TokenDataDTO tokenDataDTO = tokenDataDTOList.get(i);
            Field[] declaredFields = tokenDataDTO.getClass().getDeclaredFields();
            for (int f = 0; f < declaredFields.length; f++) {
                Field field = declaredFields[f];
                field.setAccessible(true);
                Object value = field.get(tokenDataDTO);
                Cell cell = row.createCell(f);
                cell.setCellValue(value.toString());
            }
        }
        return workbook;
    }

}
