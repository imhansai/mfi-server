package dev.fromnowon.mfiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * mfi 认证获取 token 相关数据
 *
 * @author hansai
 */
@SpringBootApplication
public class MfiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MfiServerApplication.class, args);
    }

}
