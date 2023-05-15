package dev.fromnowon.mfiserver.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public MfiService(AuthEntitiesRequestService authEntitiesRequestService) {
        this.authEntitiesRequestService = authEntitiesRequestService;
    }

    public byte[] getBytes(Integer requestedAuthEntityCount) {
        // Auth Entities Request
        try {
            authEntitiesRequestService.authEntitiesRequest(requestedAuthEntityCount);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Auth Entities Request 请求异常! " + e.getMessage(), e);
        }

        // api/v1.0/external/authEntities/{request_id}

        // api/v1.0/external/authEntities/{request_id}/{file_name}

        // api/v1.0/external/bulk/usedAuthEntities

        Workbook workbook = getWorkbook();
        // 将Workbook写入一个ByteArrayOutputStream中
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Workbook getWorkbook() {
        // 创建一个Workbook对象
        Workbook workbook = new XSSFWorkbook();

        // 创建一个Sheet对象
        Sheet sheet = workbook.createSheet("Sheet1");

        // 创建一行并设置单元格的值
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Hello Excel!");

        return workbook;
    }

}
