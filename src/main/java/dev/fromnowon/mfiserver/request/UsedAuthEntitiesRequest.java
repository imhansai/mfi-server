package dev.fromnowon.mfiserver.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Register Used Auth Entity during Factory Provisioning Request
 *
 * @author hansai
 */
@NoArgsConstructor
@Data
public class UsedAuthEntitiesRequest {

    @JsonProperty("ppid")
    private String ppid;
    @JsonProperty("auth_entities")
    private List<Map<String, String>> authEntities;

}
