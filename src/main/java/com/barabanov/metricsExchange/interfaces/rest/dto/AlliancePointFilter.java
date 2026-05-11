package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.PointStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlliancePointFilter {
    Long id;
    Long allianceId;
    List<PointStatus> statuses;
}
