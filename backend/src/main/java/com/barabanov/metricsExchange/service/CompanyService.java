package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyPageRequest;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateCompanyDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.PageResponse;
import com.barabanov.metricsExchange.mapper.CompanyMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.CompanyRepository;
import com.barabanov.metricsExchange.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyMapper companyMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


    @Transactional
    public CompanyDto createCompany(CreateCompanyDto createCompanyDto) {
        CompanyEntity creatingCompany = companyMapper.mapToEntity(createCompanyDto);
        creatingCompany.setOwner(Optional.ofNullable(createCompanyDto)
                .map(CreateCompanyDto::getOwnerUserId)
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new RuntimeException("Не удалось найти пользователя, который указан как владелец компании")));

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
    public CompanyDto getCompanyInfo(Long companyId) {
        return companyRepository.findById(companyId)
                .map(companyMapper::mapToCompanyDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию с id: %s", companyId)));
    }
}
