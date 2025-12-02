package com.pyxis.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "https://pyxis.kr",
                                "https://www.pyxis.kr",
                                "https://api.pyxis.kr",

                                // 로컬 개발용
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "https://localhost:3000",
                                "https://localhost:5173",
                                "http://192.168.100.202:3000",
                                "http://192.168.100.202:5173",
                                "https://192.168.100.202:3000",
                                "https://192.168.100.202:5173"
                        )
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}