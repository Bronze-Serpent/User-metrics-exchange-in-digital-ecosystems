package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.TransferStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferRqDto {

    Long id;
    TransferStatus status;
    OffsetDateTime createdAt;
    String fromProfileId;
    String toProfileId;
    String comment;
    String decision;
    String decisionComment;
    Long fromCompanyId;
    Long toCompanyId;
}
