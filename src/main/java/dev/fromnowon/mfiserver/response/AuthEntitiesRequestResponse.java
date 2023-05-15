package dev.fromnowon.mfiserver.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auth Entities Request Response
 *
 * @author hansai
 */
@NoArgsConstructor
@Data
public class AuthEntitiesRequestResponse {
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("download_availability")
    private String downloadAvailability;
}
