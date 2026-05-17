package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateTransferRqDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.TransferDecisionDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.TransferRqDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface TransferRequestMapper {
    TransferRequestEntity mapToEntity(CreateTransferRqDto createTransferRqDto);

    TransferRqDto mapToTransferRqDto(TransferRequestEntity source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeUpdateToTransferEntity(TransferDecisionDto source,@MappingTarget TransferRequestEntity target);
}
