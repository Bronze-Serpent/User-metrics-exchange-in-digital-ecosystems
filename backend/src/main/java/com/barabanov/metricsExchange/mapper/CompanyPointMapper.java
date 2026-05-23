package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.CompanyPointEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyPointDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyPointUpdateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateCompanyPointDto;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface CompanyPointMapper {

    CompanyPointEntity mapToEntity(CreateCompanyPointDto source);

    CompanyPointDto mapToCompanyPointDto(CompanyPointEntity source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "newPointStatus", target = "status")
    @Mapping(source = "newCompanyPointUrl", target = "url")
    void mergeUpdateToEntity(CompanyPointUpdateDto source, @MappingTarget CompanyPointEntity target);
}
