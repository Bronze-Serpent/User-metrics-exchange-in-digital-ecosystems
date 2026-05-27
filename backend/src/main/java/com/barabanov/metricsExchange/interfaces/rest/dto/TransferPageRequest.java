package com.barabanov.metricsExchange.interfaces.rest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransferPageRequest {

    Integer pageNumber;
    Integer pageSize;
    TransferFilter transferFilter;
    @Builder.Default
    List<SortSpecifier<TransferRequestSortField>> sortOrderSpecifiers = new ArrayList<>();
}
