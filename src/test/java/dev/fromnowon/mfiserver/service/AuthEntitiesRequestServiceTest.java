package dev.fromnowon.mfiserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthEntitiesRequestServiceTest {

    @Autowired
    private AuthEntitiesRequestService authEntitiesRequestService;

    @Test
    void authEntitiesRequest() throws IOException, InterruptedException {
        authEntitiesRequestService.authEntitiesRequest(1);
    }

}