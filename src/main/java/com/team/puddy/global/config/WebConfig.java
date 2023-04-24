package com.team.puddy.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://puddy.shop.s3-website.ap-northeast-2.amazonaws.com/",
                        "http://puddy.shop/", "https://dgsoy5jp5hqg3.cloudfront.net",
        "https://www.puddy.world/")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH");
    }
}
