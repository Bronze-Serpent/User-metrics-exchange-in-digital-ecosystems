package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateUserDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface UserMapper {

    UserEntity mapToEntity(CreateUserDto source);

    @Mapping(target = "linkedCompanyId", source = "source.linkedCompany.id")
    UserDto toUserDto(UserEntity source);
}
