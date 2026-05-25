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
public class UserPageRequest {

    Integer pageNumber;
    Integer pageSize;
    UserFilter userFilter;
    @Builder.Default
    List<SortSpecifier<UserSortField>> sortOrderSpecifiers = new ArrayList<>();
}
