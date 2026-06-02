package com.barabanov.metricsExchange.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyWebClient {

    private final WebClient webClient;
    @Value("${app.portfolio-exchange.company-profile-id-parameter-name:profileId}")
    private final String companyProfileIdParameterName;


    public void triggerUserPortfolioExportEndPoint(String uriAsStr, String companyProfileId) {
        URI uri = URI.create(uriAsStr);

        webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .scheme(uri.getScheme())
                                .host(uri.getHost())
                                .port(uri.getPort())
                                .path(uri.getPath())
                                .fragment(uri.getFragment())
                                .queryParam(companyProfileIdParameterName, companyProfileId)
                                .build())
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
