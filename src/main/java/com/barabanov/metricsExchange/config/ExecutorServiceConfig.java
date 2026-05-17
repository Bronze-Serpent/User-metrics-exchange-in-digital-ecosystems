package com.barabanov.metricsExchange.config;

import com.barabanov.metricsExchange.helper.DestroyableExecutorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class ExecutorServiceConfig {


    @Bean(destroyMethod = "shutdown")
    public ExecutorService userMetricsRequestsExecutorService(@Value("${app.user-metrics-requests.thread-num}") Integer executingUserMetricsRequestsThreadNum) {

        return new DestroyableExecutorService(Executors.newFixedThreadPool(executingUserMetricsRequestsThreadNum));
    }
}
