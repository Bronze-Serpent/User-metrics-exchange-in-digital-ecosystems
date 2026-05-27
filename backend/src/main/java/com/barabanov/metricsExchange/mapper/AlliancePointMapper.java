package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.AlliancePointEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointCreateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointUpdateDto;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface AlliancePointMapper {

    AlliancePointEntity mapToEntity(AlliancePointCreateDto source);

    @Mapping(target = "allianceId", source = "source.alliance.id")
    AlliancePointDto mapToAlliancePointDto(AlliancePointEntity source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "newStatus", target = "status")
    void mergeUpdateToEntity(AlliancePointUpdateDto source, @MappingTarget AlliancePointEntity target);
}
