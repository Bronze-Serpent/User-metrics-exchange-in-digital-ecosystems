package com.barabanov.metricsExchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserMetricsWebService {

    private final WebClient webClient; // TODO: Всего один webClient. Все запросы будут через него? Он не блокирется пока не будет получен запрос и может выполнять другие запросы?


    public String getUserMetricsFrom(String url, MultiValueMap<String, String> parameters) {

        //TODO: а как происходит запрос? В каком момент он становится асинхронным, если становится. Только в bodyToMono? Когда вызывает retrive
        return webClient.get()
                .uri(uriBuilder -> buildUriForUserMetricsRq(uriBuilder, url, parameters))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    private static URI buildUriForUserMetricsRq(UriBuilder uriBuilder, String url, MultiValueMap<String, String> parameters) {
        uriBuilder.host(url);

        for (MultiValueMap.Entry<String, List<String>> parameterEntry : parameters.entrySet()) {
            for (String parameterValue : parameterEntry.getValue())
                uriBuilder.queryParam(parameterEntry.getKey(), parameterValue);
        }

        return uriBuilder.build();
    }
}
