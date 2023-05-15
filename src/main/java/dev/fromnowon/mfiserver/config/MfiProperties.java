package dev.fromnowon.mfiserver.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MFi 配置
 *
 * @author hansai
 */
@ConfigurationProperties(prefix = "mfi")
@Configuration
@Data
@NoArgsConstructor
public class MfiProperties {

    /**
     * 产品计划id
     */
    private String ppid;

    /**
     * keystore 密码
     */
    private String keystorePassword;

    /**
     * keystore alias
     */
    private String alias;

}

