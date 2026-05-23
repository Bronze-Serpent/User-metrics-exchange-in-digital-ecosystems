package com.barabanov.metricsExchange.utils;

import com.barabanov.metricsExchange.kafka.dto.UserPortfolioEvent;
import lombok.NoArgsConstructor;

import java.util.Optional;


@NoArgsConstructor
public class DataExtractionUtils {


    public static Long getTransferRequestId(UserPortfolioEvent userPortfolioEvent) {
        return Optional.ofNullable(userPortfolioEvent)
                .map(UserPortfolioEvent::getTransferRequestId)
                .orElse(null);
    }
}
