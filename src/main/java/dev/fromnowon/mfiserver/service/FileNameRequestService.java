package dev.fromnowon.mfiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fromnowon.mfiserver.response.FileNameRequestResponse;
import lombok.extern.slf4j.Slf4j;
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

    public List<String> fileNameRequest(String requestId) throws IOException, InterruptedException {
        String baseUrl = "https://swa.apple.com/api/v1.0/external/authEntities/";
        String url = baseUrl + requestId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug("File Name Request Response Code: {}", response.statusCode());
        log.debug("File Name Request Response Body: {}", response.body());

        FileNameRequestResponse fileNameRequestResponse = objectMapper.readValue(response.body(), FileNameRequestResponse.class);
        return fileNameRequestResponse.getFileName();
    }

}
