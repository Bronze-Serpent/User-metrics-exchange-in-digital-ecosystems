package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFilter {

    Long userId;
    String userEmailSubstr; //TODO: дописать реализацию для этих фильтров
    Long linkedCompanyId;
    UserRole userRole;
}
