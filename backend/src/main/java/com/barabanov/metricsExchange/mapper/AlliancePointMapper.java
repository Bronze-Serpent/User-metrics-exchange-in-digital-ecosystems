package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.AlliancePointEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointCreateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface AlliancePointMapper {

    AlliancePointEntity mapToEntity(AlliancePointCreateDto source);

    AlliancePointDto mapToAlliancePointDto(AlliancePointEntity source);
}
