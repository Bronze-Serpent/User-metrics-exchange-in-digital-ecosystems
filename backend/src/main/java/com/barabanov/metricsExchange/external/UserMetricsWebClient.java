package com.barabanov.metricsExchange.external;

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
public class UserMetricsWebClient {

    private final WebClient webClient; // TODO: Всего один webClient. Все запросы будут через него? Он не блокирется пока не будет получен запрос и может выполнять другие запросы?


    public String getUserMetricsFrom(String uriAsStr, MultiValueMap<String, String> parameters) {

        //TODO: а как происходит запрос? В каком момент он становится асинхронным, если становится. Только в bodyToMono? Когда вызывает retrive
        return webClient.get()
                .uri(uriBuilder -> buildUriForUserMetricsRq(uriBuilder, uriAsStr, parameters))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    //TODO: уточнить нейминг. Что есть URI, а что URL
    private static URI buildUriForUserMetricsRq(UriBuilder uriBuilder, String uriAsStr, MultiValueMap<String, String> parameters) {
        URI uri = URI.create(uriAsStr);

        uriBuilder.scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .path(uri.getPath())
                .fragment(uri.getFragment());

        for (MultiValueMap.Entry<String, List<String>> parameterEntry : parameters.entrySet()) {
            for (String parameterValue : parameterEntry.getValue())
                uriBuilder.queryParam(parameterEntry.getKey(), parameterValue);
        }

        return uriBuilder.build();
    }

}
