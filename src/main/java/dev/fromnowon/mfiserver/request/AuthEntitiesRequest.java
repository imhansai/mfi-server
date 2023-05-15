package dev.fromnowon.mfiserver.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auth Entities Request
 *
 * @author hansai
 */
@Data
@NoArgsConstructor
public class AuthEntitiesRequest {

    private String ppid;

    @JsonProperty("requested_auth_entity_count")
    private Integer requestedAuthEntityCount;

}
