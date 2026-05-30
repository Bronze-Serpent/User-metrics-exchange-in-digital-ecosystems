package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateUserDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserRegisterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface UserMapper {

    UserEntity mapToEntity(CreateUserDto source);

    UserEntity mapToEntity(UserRegisterDto userRegisterDto);

    @Mapping(target = "linkedCompanyId", source = "source.linkedCompany.id")
    UserDto toUserDto(UserEntity source);


    default User toSpringSecUser(UserEntity source) {
        if (source == null)
            return null;

        return new User(source.getEmail(), source.getPasswordHash(), Collections.singleton(source.getRole()));
    }
}
