package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.entity.TransferDecision;
import com.barabanov.metricsExchange.entity.TransferStatus;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.mapper.CompanyMapper;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.repository.CompanyPointRepository;
import com.barabanov.metricsExchange.repository.CompanyRepository;
import com.barabanov.metricsExchange.repository.TransferRequestRepository;
import com.barabanov.metricsExchange.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final TransferRequestRepository transferRequestRepository;
    private final UserRepository userRepository;
    private final CompanyPointRepository companyPointRepository;
    private final CompanyMapper companyMapper;
    private final PredicateDataMapper predicateDataMapper;


    @Transactional
    public CompanyDto createCompany(CreateCompanyDto createCompanyDto) {
        CompanyEntity creatingCompany = companyMapper.mapToEntity(createCompanyDto);

        return companyMapper.mapToCompanyDto(companyRepository.save(creatingCompany));
    }


    @Transactional
    public void deleteCompany(Long companyId) {

        CompanyEntity deletingCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию с id: %s", companyId)));

        userRepository.removeAllWith(companyId);
        //TODO: удалять не так, а вызывая метод closeTransferRequest в сервисе transferRequest, но делая это батчами, в цикле чтобы могла отрабатывать доп логика на закрытие. Например отправка уведомлений и т.д.
        // и текст вынести в Value
        transferRequestRepository.setDecisionStatusCommentAllWith(TransferDecision.REJECT, TransferStatus.CLOSED,
                String.format("Заявка закрыта по причине удаления компании с именем: %s из системы", deletingCompany.getName()), companyId);
        companyPointRepository.removeAllWith(companyId);

        /**
         * Используется мягкое удаление для компании, поскольку на неё будут продолжать ссылаться заявки пользователей,
         * которые удалять нельзя. Остальная же информация по компании удаляется
         */
        deletingCompany.setIsDeleted(true);
        companyRepository.save(deletingCompany);
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
    public List<CompanyIdNameDto> getIdNameSummary(CompanyIdNameSummaryRq companyIdNameSummaryRq) {
        Predicate predicate = predicateDataMapper.mapCompanyFilterToPredicate(companyIdNameSummaryRq.getCompanyFilter());

        return companyRepository.findCompaniesWith(predicate,
                        companyIdNameSummaryRq.getPageSize(),
                        companyIdNameSummaryRq.getPageNumber() * companyIdNameSummaryRq.getPageSize()).stream()
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
