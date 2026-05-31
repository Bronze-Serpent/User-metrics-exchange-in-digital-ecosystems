package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.entity.CompanyPointEntity;
import com.barabanov.metricsExchange.entity.PointStatus;
import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.mapper.CompanyPointMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.CompanyPointRepository;
import com.barabanov.metricsExchange.repository.UserRepository;
import com.barabanov.metricsExchange.utils.QPredicates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.barabanov.metricsExchange.entity.QCompanyPointEntity.companyPointEntity;
import static com.barabanov.metricsExchange.entity.UserRole.COMPANY_AGENT;
import static com.barabanov.metricsExchange.utils.DataExtractionUtils.getUserRole;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyPointService {

    private final UserRepository userRepository;
    private final CompanyPointRepository companyPointRepository;
    private final CompanyPointMapper companyPointMapper;
    private final PredicateDataMapper predicateDataMapper;


    @Transactional
    public CompanyPointDto createCompanyPoint(CreateCompanyPointDto createCompanyPointDto, String creatorUserEmail) {

        CompanyEntity linkedCompanyEntity = userRepository.findByEmail(creatorUserEmail)
                .map(UserEntity::getLinkedCompany)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию связанную с email: %s",
                        creatorUserEmail)));

        CompanyPointEntity companyPointEntity = companyPointMapper.mapToEntity(createCompanyPointDto);
        companyPointEntity.setStatus(PointStatus.NEW);
        companyPointEntity.setCompany(linkedCompanyEntity);

        return companyPointMapper.mapToCompanyPointDto(companyPointRepository.save(companyPointEntity));
    }


    @Transactional
    public void deleteCompanyPoint(Long companyPointId) {
        companyPointRepository.deleteById(companyPointId);
    }


    @Transactional(readOnly = true)
    public PageResponse<CompanyPointDto> getCompanyPointPage(CompanyPointPageRequest companyPointPageRequest, UserDetails userDetails) {

        QPredicates predicateBuilder = predicateDataMapper.mapCompanyPointFilterToPredicate(companyPointPageRequest.getCompanyPointFilter());
        if (COMPANY_AGENT == getUserRole(userDetails)) {
            Long linkedCompanyId = Optional.ofNullable(userDetails.getUsername())
                    .flatMap(userRepository::findByEmail)
                    .map(UserEntity::getLinkedCompany)
                    .map(CompanyEntity::getId)
                    .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию связанную с email: %s",
                            userDetails.getUsername())));
            predicateBuilder.add(linkedCompanyId, companyPointEntity.company.id::eq);
        }

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(companyPointPageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(companyPointPageRequest.getPageSize())
                .orElseThrow());
        Page<CompanyPointEntity> companyPointsPage = companyPointRepository.findAll(predicateBuilder.build(), pageRequest);

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
