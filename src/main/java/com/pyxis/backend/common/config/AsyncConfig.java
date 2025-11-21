package com.pyxis.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);      // 기본 쓰레드 수
        executor.setMaxPoolSize(16);      // 최대 쓰레드 수
        executor.setQueueCapacity(200);   // 큐 사이즈
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
