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

    @Value("${external.fastapi-url-8000}")
    private String fastapi8000;

    @Value("${external.fastapi-url-9000}")
    private String fastapi9000;

    private HttpClient createHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))
                );
    }

    @Bean
    public WebClient fastApi8000Client() {
        return WebClient.builder()
                .baseUrl(fastapi8000)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient fastApi9000Client() {
        return WebClient.builder()
                .baseUrl(fastapi9000)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient kakaoWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .responseTimeout(Duration.ofSeconds(30))
                ))
                .build();
    }

    @Bean
    public WebClient naverWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .responseTimeout(Duration.ofSeconds(30))
                ))
                .build();
    }
}