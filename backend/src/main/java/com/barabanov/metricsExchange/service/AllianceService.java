package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.AllianceEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.mapper.AllianceMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.AllianceRepository;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class AllianceService {

    private final AllianceMapper allianceMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final AllianceRepository allianceRepository;


    @Transactional
    public AllianceDto createAlliance(CreateAllianceDto createAllianceDto) {
        AllianceEntity allianceEntity = allianceMapper.mapToEntity(createAllianceDto);

        return allianceMapper.mapToAllianceDto(allianceRepository.save(allianceEntity));
    }


    @Transactional
    public void deleteAlliance(Long allianceId) {
        allianceRepository.deleteById(allianceId);
    }


    @Transactional(readOnly = true)
    public PageResponse<AllianceDto> getAlliancePage(AlliancePageRequest alliancePageRequest) {
        Predicate predicate = predicateDataMapper.mapAllianceFilterToPredicate(alliancePageRequest.getAllianceFilter());

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(alliancePageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(alliancePageRequest.getPageSize())
                .orElseThrow());
        Page<AllianceEntity> alliancePage = allianceRepository.findAll(predicate, pageRequest);

        return PageResponse.<AllianceDto>builder()
                .data(alliancePage.getContent().stream().map(allianceMapper::mapToAllianceDto)
                        .toList())
                .pageNumber(alliancePage.getNumber())
                .totalElements(alliancePage.getTotalElements())
                .totalPages(alliancePage.getTotalPages())
                .build();
    }


    @Transactional
    public AllianceDto updateAlliance(Long allianceId, AllianceUpdateDto allianceUpdateDto) {
        AllianceEntity allianceEntity = allianceRepository.findById(allianceId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти альянс с id: %s", allianceId)));

        allianceMapper.mergeUpdateToEntity(allianceUpdateDto, allianceEntity);
        return null;
    }

    @Transactional(readOnly = true)
    public AllianceDto getAllianceInfo(Long allianceId) {
        return allianceRepository.findById(allianceId)
                .map(allianceMapper::mapToAllianceDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти альянс с id: %s", allianceId)));
    }
}
