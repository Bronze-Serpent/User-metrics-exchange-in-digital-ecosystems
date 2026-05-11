package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.AllianceEntity;
import com.barabanov.metricsExchange.entity.PointStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlliancePointDto {
    Long id;
    String format;
    PointStatus status;
    AllianceEntity alliance;
    List<CompanyPointDto> companyPoints;
}
