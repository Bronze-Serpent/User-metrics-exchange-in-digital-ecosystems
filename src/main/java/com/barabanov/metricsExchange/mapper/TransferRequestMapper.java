package com.barabanov.metricsExchange.mapper;

import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateTransferRqDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.TransferRqDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface TransferRequestMapper {
    TransferRequestEntity mapToEntity(CreateTransferRqDto createTransferRqDto);

    TransferRqDto mapToTransferRqDto(TransferRequestEntity source);
}
