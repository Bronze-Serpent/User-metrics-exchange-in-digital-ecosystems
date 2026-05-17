package com.barabanov.metricsExchange.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPortfolioEvent {
    private Long transferRequestId;
    private String profileId;
    private UserPortfolio userPortfolio;
}