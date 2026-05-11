package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.TransferDecisionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferFilter {

    Long id;
    String fromProfileId;
    String toProfileId;
    List<TransferDecisionType> decisions;
    Long fromCompanyId;
    Long toCompanyId;
}
