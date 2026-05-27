package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyPointEntity;
import com.barabanov.metricsExchange.entity.PointStatus;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.mapper.CompanyPointMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.CompanyPointRepository;
import com.barabanov.metricsExchange.repository.CompanyRepository;
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
public class CompanyPointService {

    private final CompanyPointRepository companyPointRepository;
    private final CompanyRepository companyRepository;
    private final CompanyPointMapper companyPointMapper;
    private final PredicateDataMapper predicateDataMapper;


    @Transactional
    public CompanyPointDto createCompanyPoint(CreateCompanyPointDto createCompanyPointDto) {
        CompanyPointEntity companyPointEntity = companyPointMapper.mapToEntity(createCompanyPointDto);
        companyPointEntity.setStatus(PointStatus.NEW);
        companyPointEntity.setCompany(Optional.ofNullable(createCompanyPointDto.getCompanyId())
                .flatMap(companyRepository::findById)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Не удалось найти компанию, которой принадлежит эта точка: %s", createCompanyPointDto.getCompanyId()))));

        return companyPointMapper.mapToCompanyPointDto(companyPointRepository.save(companyPointEntity));
    }


    @Transactional
    public void deleteCompanyPoint(Long companyPointId) {
        companyPointRepository.deleteById(companyPointId);
    }


    @Transactional(readOnly = true)
    public PageResponse<CompanyPointDto> getCompanyPointPage(CompanyPointPageRequest companyPointPageRequest) {
        Predicate predicate = predicateDataMapper.mapCompanyPointFilterToPredicate(companyPointPageRequest.getCompanyPointFilter());

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(companyPointPageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(companyPointPageRequest.getPageSize())
                .orElseThrow());
        Page<CompanyPointEntity> companyPointsPage = companyPointRepository.findAll(predicate, pageRequest);

        return PageResponse.<CompanyPointDto>builder()
                .data(companyPointsPage.getContent().stream().map(companyPointMapper::mapToCompanyPointDto)
                        .toList())
                .pageNumber(companyPointsPage.getNumber())
                .totalElements(companyPointsPage.getTotalElements())
                .totalPages(companyPointsPage.getTotalPages())
                .build();
    }


    @Transactional
    public CompanyPointDto updateCompanyPoint(Long companyPointId, CompanyPointUpdateDto companyPointUpdateDto) {
        CompanyPointEntity updatingCompanyPoint = companyPointRepository.findById(companyPointId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку компании с id: %s", companyPointId)));
        companyPointMapper.mergeUpdateToEntity(companyPointUpdateDto, updatingCompanyPoint);

        return companyPointMapper.mapToCompanyPointDto(companyPointRepository.save(updatingCompanyPoint));
    }


    @Transactional(readOnly = true)
    public CompanyPointDto getCompanyPointInfo(Long companyPointId) {
        return companyPointRepository.findById(companyPointId)
                .map(companyPointMapper::mapToCompanyPointDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти точку компании с id: %s", companyPointId)));
    }
}
