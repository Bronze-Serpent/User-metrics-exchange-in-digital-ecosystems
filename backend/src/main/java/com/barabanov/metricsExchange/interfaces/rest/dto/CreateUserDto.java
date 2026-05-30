package com.barabanov.metricsExchange.interfaces.rest.dto;

import com.barabanov.metricsExchange.entity.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateUserDto {

    String email;
    UserRole role;
    Long linkedCompanyId;
    String password; // TODO: сделать так, чтобы у пользователей которых регистрируют в системе а не у клиентов не передавался пароль, а он генерировался в backend и возвращался в ответе + как раз в SecurityConfig будет /user/registration И user/create Разные с разными уровнями доступа и эти Url будут принимать разные ДТО (на регистрацию роли не нужны)
}
