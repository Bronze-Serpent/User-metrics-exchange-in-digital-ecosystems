package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.TransferDecision;
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
    Long userId;
    String fromProfileId;
    String toProfileId;
    List<TransferDecision> decisions;
    Long fromCompanyId;
    Long toCompanyId;
}
