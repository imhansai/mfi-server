package dev.fromnowon.mfiserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * token 相关数据
 *
 * @author hansai
 */
@Data
@NoArgsConstructor
public class TokenDataDTO1 {

    private String ppid;

    private String token;

    private String tokenBase64;

    private String tokenHex;

    private String uuid;

    private String productData;

}
