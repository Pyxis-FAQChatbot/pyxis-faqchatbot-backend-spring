package com.pyxis.backend.common.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {



    @Value("${external.fastapi-url}")
    private String fastapiUrl;

    @Bean
    public WebClient webClient() {
        // HttpClient 설정
        HttpClient httpClient = HttpClient.create()
                // 연결 타임아웃: 서버 연결 시도 시간 (5초)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

                // 응답 타임아웃: 전체 응답 대기 시간 (60초 - AI는 느림)
                .responseTimeout(Duration.ofSeconds(30))

                // Read/Write 타임아웃
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))   // 읽기: 60초
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))  // 쓰기: 10초
                );

        // WebClient 빌드
        return WebClient.builder()
                .baseUrl(fastapiUrl)
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}