package dev.fromnowon.mfiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * mfi 认证获取 token 相关数据
 *
 * @author hansai
 */
@SpringBootApplication
@EnableRetry
public class MfiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MfiServerApplication.class, args);
    }

}
