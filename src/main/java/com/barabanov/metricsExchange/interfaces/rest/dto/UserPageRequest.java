package com.barabanov.metricsExchange.interfaces.rest.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPageRequest {

    Integer pageNumber;
    Integer pageSize;
    UserFilter userFilter;
    UserSortField sortBy;
    SortOrder sortOrder;
}
