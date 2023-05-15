package dev.fromnowon.mfiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fromnowon.mfiserver.config.MfiProperties;
import dev.fromnowon.mfiserver.request.AuthEntitiesRequest;
import dev.fromnowon.mfiserver.response.AuthEntitiesRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Auth Entities Request
 *
 * @author hansai
 */
@Service
@Slf4j
@Validated
public class AuthEntitiesRequestService {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final MfiProperties mfiProperties;

    public AuthEntitiesRequestService(HttpClient httpClient,
                                      ObjectMapper objectMapper,
                                      MfiProperties mfiProperties) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.mfiProperties = mfiProperties;
    }

    public String authEntitiesRequest(Integer requestedAuthEntityCount) throws IOException, InterruptedException {
        String url = "https://swa.apple.com:443/api/v1.0/external/authEntityRequests";

        AuthEntitiesRequest authEntitiesRequest = new AuthEntitiesRequest();
        authEntitiesRequest.setRequestedAuthEntityCount(requestedAuthEntityCount);
        authEntitiesRequest.setPpid(mfiProperties.getPpid());

        // 构建请求的 JSON 数据
        String jsonBody = objectMapper.writeValueAsString(authEntitiesRequest);

        // 创建 HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // 发送请求并获取响应
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // 获取响应状态码
        int statusCode = response.statusCode();
        log.debug("Status Code: {}", statusCode);

        // 获取响应头
        HttpHeaders headers = response.headers();
        headers.map().forEach((key, value) -> log.debug("{}: {}", key, value));

        // 获取响应体
        String responseBody = response.body();
        log.debug("Response Body: {}", responseBody);

        AuthEntitiesRequestResponse authEntitiesRequestResponse = objectMapper.readValue(responseBody, AuthEntitiesRequestResponse.class);
        return authEntitiesRequestResponse.getRequestId();
    }

}
