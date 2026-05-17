package com.barabanov.metricsExchange.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
public class WebClientConfig {

    @Bean
    public HttpClient httpClient(@Value("${http-requests.conection-timeout-ms:30000}") Integer connectionTimeoutMs,
                                 @Value("${http-requests.response-timeout-ms:60000}") Integer responseTimeoutMs,
                                 @Value("${http-requests.read-timeout-ms:30000}") Integer readTimeoutMs,
                                 @Value("${http-requests.write-timeout-ms:30000}") Integer writeTimeoutMs) {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMs)
                .responseTimeout(Duration.ofMillis(responseTimeoutMs))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeoutMs, TimeUnit.MILLISECONDS))
                );
    }

    @Bean
    public WebClient webClient(@Value("${http-requests.max-in-memory-size-bytes:-1}") Integer maxInMemorySize, HttpClient httpClient) {

        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(maxInMemorySize)) // 256КБ по умолчанию
                        .build())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
