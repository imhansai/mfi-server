package dev.fromnowon.mfiserver.service;

import dev.fromnowon.mfiserver.exception.SystemException;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * File Download
 *
 * @author hansai
 */
@Service
@Slf4j
@Validated
public class FileDownloadService {

    private final HttpClient httpClient;

    private final ResourceLoader resourceLoader;

    public FileDownloadService(HttpClient httpClient,
                               ResourceLoader resourceLoader) {
        this.httpClient = httpClient;
        this.resourceLoader = resourceLoader;
    }

    public List<Path> fileDownload(String requestId, @NotEmpty List<String> fileNameList) {
        try {
            return getFilePathList(requestId, fileNameList);
        } catch (IOException | InterruptedException e) {
            throw new SystemException("File Download Error" + e.getMessage(), e);
        }
    }

    private List<Path> getFilePathList(String requestId, List<String> fileNameList) throws IOException, InterruptedException {
        List<Path> filePathList = new ArrayList<>();
        for (String fileName : fileNameList) {
            String baseUrl = "https://swa.apple.com/api/v1.0/external/authEntities/";
            String url = baseUrl + requestId + "/" + fileName;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();

            Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX);
            Path dir = Paths.get(resource.getURI());
            HttpResponse<Path> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFileDownload(dir, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            Path filePath = response.body();
            log.debug("File Download Response Code: {}", response.statusCode());
            log.debug("File Download Response Code: {}", filePath);
            filePathList.add(filePath);
        }

        return filePathList;
    }

}
