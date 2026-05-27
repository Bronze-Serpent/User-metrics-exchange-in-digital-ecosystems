package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.AllianceEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.AllianceDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AllianceUpdateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateAllianceDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface AllianceMapper {

    AllianceEntity mapToEntity(CreateAllianceDto source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeUpdateToEntity(AllianceUpdateDto source, @MappingTarget AllianceEntity target);

    AllianceDto mapToAllianceDto(AllianceEntity source);
}
