package com.barabanov.metricsExchange.interfaces.rest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferRqDto {

    Long id;
    String fromProfileId;
    String toProfileId;
    String comment;
    String decision;
    String decisionComment;
    Long fromCompanyId;
    Long toCompanyId;
}
