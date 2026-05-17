package com.barabanov.metricsExchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyWebService {

    private final WebClient webClient;
    @Value("app.portfolio-exchange.path:/portfolio")
    private final String path;
    @Value("app.portfolio-exchange.company-profile-id-parameter-name:profileId")
    private final String companyProfileIdParameterName;

    public void triggerUserPortfolioExportEndPoint(String host, String companyProfileId) {
        webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .host(host)
                                .path(path)
                                .queryParam(companyProfileIdParameterName, companyProfileId)
                                .build())
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
