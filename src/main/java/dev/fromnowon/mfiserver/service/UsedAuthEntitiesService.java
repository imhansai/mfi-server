package dev.fromnowon.mfiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fromnowon.mfiserver.exception.SystemException;
import dev.fromnowon.mfiserver.request.UsedAuthEntitiesRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 注册
 */
@Service
@Slf4j
@Validated
public class UsedAuthEntitiesService {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public UsedAuthEntitiesService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public void usedAuthEntities(UsedAuthEntitiesRequest usedAuthEntitiesRequest) {
        try {
            execute(usedAuthEntitiesRequest);
        } catch (IOException | InterruptedException e) {
            throw new SystemException("注册服务 Error" + e.getMessage(), e);
        }
    }

    private void execute(UsedAuthEntitiesRequest usedAuthEntitiesRequest) throws IOException, InterruptedException {
        String url = "https://swa.apple.com/api/v1.0/external/bulk/usedAuthEntities";
        String requestBodyJson = objectMapper.writeValueAsString(usedAuthEntitiesRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.debug("注册 Status Code: {}", response.statusCode());
        log.debug("注册 Response body: {}", response.body());
    }

}
