package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.PointStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyPointUpdateDto {

    PointStatus newPointStatus;
    String newCompanyPointUrl;
}
