package com.example.captcha_demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "captcha")
@Data
public class CaptchaYml {
    public Integer width;
    public Integer height;
    public Session session;

    @Data
    public static class Session {
        public String key;
        public String date;
    }
}
