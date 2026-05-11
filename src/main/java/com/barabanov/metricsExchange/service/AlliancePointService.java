package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.AlliancePointEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointCreateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointPageRequest;
import com.barabanov.metricsExchange.interfaces.rest.dto.PageResponse;
import com.barabanov.metricsExchange.mapper.AlliancePointMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.AlliancePointRepository;
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
public class AlliancePointService {
    private final AlliancePointRepository alliancePointRepository;
    private final AlliancePointMapper alliancePointMapper;
    private final PredicateDataMapper predicateDataMapper;


    @Transactional
    public AlliancePointDto createAlliancePoint(AlliancePointCreateDto alliancePointCreateDto) {
        AlliancePointEntity creatingAlliancePoint = alliancePointMapper.mapToEntity(alliancePointCreateDto);

        return alliancePointMapper.mapToAlliancePointDto(alliancePointRepository.save(creatingAlliancePoint));
    }


    @Transactional(readOnly = true)
    public AlliancePointDto getAlliancePointInfo(Long alliancePointId) {
        return alliancePointRepository.findById(alliancePointId)
                .map(alliancePointMapper::mapToAlliancePointDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку альянса с id: %s", alliancePointId)));
    }


    @Transactional
    public void deleteAlliancePointInfo(Long alliancePointId) {
        alliancePointRepository.deleteById(alliancePointId);
    }


    @Transactional(readOnly = true)
    public PageResponse<AlliancePointDto> getAlliancePointsPage(AlliancePointPageRequest alliancePointPageRequest) {
        Predicate predicate = predicateDataMapper.mapAlliancePointFilterToPredicate(alliancePointPageRequest.getAlliancePointFilter());

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(alliancePointPageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(alliancePointPageRequest.getPageSize())
                .orElseThrow());
        Page<AlliancePointEntity> alliancePointsPage = alliancePointRepository.findAll(predicate, pageRequest);


        return PageResponse.<AlliancePointDto>builder()
                .data(alliancePointsPage.getContent().stream().map(alliancePointMapper::mapToAlliancePointDto)
                        .toList())
                .pageNumber(alliancePointsPage.getNumber())
                .totalElements(alliancePointsPage.getTotalElements())
                .totalPages(alliancePointsPage.getTotalPages())
                .build();
    }


    @Transactional
    public AlliancePointDto addCompanyPointFor(Long alliancePointId, Long companyPointId) {
        return null;
    }

    @Transactional
    public AlliancePointDto deleteCompanyPointFrom(Long alliancePointId, Long companyPointId) {
        return null;
    }
}
