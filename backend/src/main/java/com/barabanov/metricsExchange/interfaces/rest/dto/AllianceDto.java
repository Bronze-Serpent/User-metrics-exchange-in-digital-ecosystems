package com.barabanov.metricsExchange.interfaces.rest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AllianceDto {

    Long id;
    String name;
    String description;
    List<AlliancePointDto> points;
}
