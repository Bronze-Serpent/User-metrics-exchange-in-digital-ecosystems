package com.barabanov.metricsExchange.interfaces.rest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlliancePageRequest {

    Integer pageNumber;
    Integer pageSize;
    AllianceFilter allianceFilter;
    SortOrder sortOrder;
}
