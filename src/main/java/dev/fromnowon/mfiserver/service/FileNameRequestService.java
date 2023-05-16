package dev.fromnowon.mfiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fromnowon.mfiserver.exception.SystemException;
import dev.fromnowon.mfiserver.response.FileNameRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * File Name Request
 *
 * @author hansai
 */
@Service
@Slf4j
@Validated
public class FileNameRequestService {

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public FileNameRequestService(HttpClient httpClient,
                                  ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Retryable(backoff = @Backoff(delay = 500, multiplier = 1, maxDelay = 5000))
    public List<String> fileNameRequest(String requestId) {
        try {
            return getFileNameList(requestId);
        } catch (IOException | InterruptedException e) {
            throw new SystemException("File Name Request Error" + e.getMessage(), e);
        }
    }

    private List<String> getFileNameList(String requestId) throws IOException, InterruptedException {
        String baseUrl = "https://swa.apple.com/api/v1.0/external/authEntities/";
        String url = baseUrl + requestId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        log.debug("File Name Request Response Code: {}", statusCode);
        String responseBody = response.body();
        log.debug("File Name Request Response Body: {}", responseBody);

        if (statusCode < HttpStatus.OK.value() || statusCode >= HttpStatus.BAD_REQUEST.value()) {
            throw new RuntimeException("File Name Request 请求失败，状态码：" + statusCode + " 响应体：" + responseBody);
        }

        FileNameRequestResponse fileNameRequestResponse = objectMapper.readValue(responseBody, FileNameRequestResponse.class);
        return fileNameRequestResponse.getFileName();
    }

}
