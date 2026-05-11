package com.barabanov.metricsExchange.interfaces.rest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlliancePointCreateDto {
    String format;
    Long allianceId;
    //TODO: при создании проставить статус и проверить что у точки компании статус тоже проставляется
}
