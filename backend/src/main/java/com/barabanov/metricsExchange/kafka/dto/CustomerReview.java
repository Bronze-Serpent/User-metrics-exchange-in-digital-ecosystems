package com.barabanov.metricsExchange.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReview {

    private Integer score; // по 10 бальной системе
    private String positive;
    private String negative;
    private String comment;
}
