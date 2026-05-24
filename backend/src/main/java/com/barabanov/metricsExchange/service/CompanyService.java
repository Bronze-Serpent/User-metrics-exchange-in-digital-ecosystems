package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.mapper.CompanyMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.CompanyRepository;
import com.barabanov.metricsExchange.utils.QPredicates;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyMapper companyMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final CompanyRepository companyRepository;


    @Transactional
    public CompanyDto createCompany(CreateCompanyDto createCompanyDto) {
        CompanyEntity creatingCompany = companyMapper.mapToEntity(createCompanyDto);

        return companyMapper.mapToCompanyDto(companyRepository.save(creatingCompany));
    }


    @Transactional
    public void deleteCompany(Long companyId) {
        companyRepository.deleteById(companyId);
    }


    @Transactional(readOnly = true)
    public PageResponse<CompanyDto> getCompanyPage(CompanyPageRequest companyPageRequest) {
        Predicate predicate = predicateDataMapper.mapCompanyFilterToPredicate(companyPageRequest.getCompanyFilter());

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(companyPageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(companyPageRequest.getPageSize())
                .orElseThrow());
        Page<CompanyEntity> companiesPage = companyRepository.findAll(predicate, pageRequest);

        return PageResponse.<CompanyDto>builder()
                .data(companiesPage.getContent().stream().map(companyMapper::mapToCompanyDto)
                        .toList())
                .pageNumber(companiesPage.getNumber())
                .totalElements(companiesPage.getTotalElements())
                .totalPages(companiesPage.getTotalPages())
                .build();
    }


    @Transactional(readOnly = true)
    public List<CompanyIdNameDto> getIdNameSummary(Boolean suppUserProfileExchangeFilter) {
        Predicate predicate = predicateDataMapper.mapToSupportUserExchangeCompanyFilter(suppUserProfileExchangeFilter);
        return StreamSupport.stream(companyRepository.findAll(predicate).spliterator(), false)
                .map(companyMapper::mapToCompanyIdNameDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public CompanyDto getCompanyInfo(Long companyId) {
        return companyRepository.findById(companyId)
                .map(companyMapper::mapToCompanyDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию с id: %s", companyId)));
    }

}
