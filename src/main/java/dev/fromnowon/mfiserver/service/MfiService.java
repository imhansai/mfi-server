package dev.fromnowon.mfiserver.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import dev.fromnowon.mfiserver.config.MfiProperties;
import dev.fromnowon.mfiserver.dto.TokenDataDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MFi 根据 ppid 和 requested_auth_entity_count 导出 excel
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

    private final MfiProperties mfiProperties;

    public MfiService(AuthEntitiesRequestService authEntitiesRequestService,
                      FileNameRequestService fileNameRequestService,
                      FileDownloadService fileDownloadService,
                      MfiProperties mfiProperties) {
        this.authEntitiesRequestService = authEntitiesRequestService;
        this.fileNameRequestService = fileNameRequestService;
        this.fileDownloadService = fileDownloadService;
        this.mfiProperties = mfiProperties;
    }

    public byte[] getBytes(Integer requestedAuthEntityCount) {
        // Auth Entities Request
        String requestId;
        try {
            requestId = authEntitiesRequestService.authEntitiesRequest(requestedAuthEntityCount);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Auth Entities Request 请求异常! " + e.getMessage(), e);
        }

        // File Name Request
        List<String> fileNameList;
        try {
            fileNameList = fileNameRequestService.fileNameRequest(requestId);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("File Name Request 请求异常! " + e.getMessage(), e);
        }

        // File Download
        List<Path> filePathList;
        try {
            filePathList = fileDownloadService.fileDownload(requestId, fileNameList);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("File Download 请求异常! " + e.getMessage(), e);
        }

        // 获取生成 ppid、Token ID、Base64-encoded Token、CRC32 in HEX、UUID(需要生成)、Product Data
        List<TokenDataDTO> tokenDataDTOList;
        try {
            tokenDataDTOList = generateTokenDataDTOList(filePathList);
        } catch (IOException e) {
            throw new RuntimeException("生成 token 相关数据异常! " + e.getMessage(), e);
        }

        // 构造方法参数

        // Register Used Auth Entity during Factory Provisioning


        Workbook workbook;
        try {
            workbook = getWorkbook(tokenDataDTOList);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("生成 workbook 异常! " + e.getMessage(), e);
        }
        // 将Workbook写入一个ByteArrayOutputStream中
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("把 workbook 写到输出流中异常!" + e.getMessage(), e);
        }
    }

    public List<TokenDataDTO> generateTokenDataDTOList(List<Path> filePathList) throws IOException {
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

    public Workbook getWorkbook(List<TokenDataDTO> tokenDataDTOList) throws IllegalAccessException {
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
