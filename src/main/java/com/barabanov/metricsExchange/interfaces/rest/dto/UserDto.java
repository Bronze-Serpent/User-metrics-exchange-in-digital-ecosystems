package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDto {

    Long id;
    String email;
    UserRole role;
}
