package dev.fromnowon.mfiserver.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * File Name Request Response
 *
 * @author hansai
 */
@NoArgsConstructor
@Data
public class FileNameRequestResponse {
    @JsonProperty("file_count")
    private Integer fileCount;
    @JsonProperty("file_name")
    private List<String> fileName;
}
