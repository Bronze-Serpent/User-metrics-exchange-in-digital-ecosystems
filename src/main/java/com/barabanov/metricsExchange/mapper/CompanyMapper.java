package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateCompanyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface CompanyMapper {

    CompanyEntity mapToEntity(CreateCompanyDto source);

    @Mapping(source = "owner.id", target = "ownerUserId")
    CompanyDto mapToCompanyDto(CompanyEntity source);
}
