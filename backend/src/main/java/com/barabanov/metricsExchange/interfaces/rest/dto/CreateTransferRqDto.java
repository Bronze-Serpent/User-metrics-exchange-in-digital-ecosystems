package com.barabanov.metricsExchange.interfaces.rest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateTransferRqDto {

    String fromProfileId;
    String toProfileId;
    String comment;
    Long fromCompanyId;
    Long toCompanyId;
}
