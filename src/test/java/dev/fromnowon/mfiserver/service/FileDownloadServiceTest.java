package dev.fromnowon.mfiserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class FileDownloadServiceTest {

    @Autowired
    private FileDownloadService fileDownloadService;

    @Test
    void fileDownload() throws IOException, InterruptedException {
        fileDownloadService.fileDownload("requestId", List.of("part1.csv"));
    }

}