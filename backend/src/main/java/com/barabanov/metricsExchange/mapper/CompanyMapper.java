package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyIdNameDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateCompanyDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface CompanyMapper {

    CompanyEntity mapToEntity(CreateCompanyDto source);

    CompanyDto mapToCompanyDto(CompanyEntity source);

    CompanyIdNameDto mapToCompanyIdNameDto(CompanyEntity source);
}
