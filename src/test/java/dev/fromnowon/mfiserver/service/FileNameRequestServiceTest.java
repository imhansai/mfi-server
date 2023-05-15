package dev.fromnowon.mfiserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class FileNameRequestServiceTest {

    @Autowired
    private FileNameRequestService fileNameRequestService;

    @Test
    void fileNameRequest() throws IOException, InterruptedException {
        fileNameRequestService.fileNameRequest("requestId");
    }

}