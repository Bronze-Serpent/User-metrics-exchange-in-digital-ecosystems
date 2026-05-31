package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import com.barabanov.metricsExchange.entity.UserEntity;
import com.barabanov.metricsExchange.entity.UserRole;
import com.barabanov.metricsExchange.external.CompanyWebClient;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.kafka.KafkaSender;
import com.barabanov.metricsExchange.kafka.dto.UserPortfolioEvent;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.mapper.SortDataMapper;
import com.barabanov.metricsExchange.mapper.TransferRequestMapper;
import com.barabanov.metricsExchange.repository.CompanyRepository;
import com.barabanov.metricsExchange.repository.TransferRequestRepository;
import com.barabanov.metricsExchange.repository.UserRepository;
import com.barabanov.metricsExchange.utils.QPredicates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.barabanov.metricsExchange.entity.QTransferRequestEntity.transferRequestEntity;
import static com.barabanov.metricsExchange.entity.TransferStatus.*;
import static com.barabanov.metricsExchange.entity.UserRole.*;
import static com.barabanov.metricsExchange.utils.DataExtractionUtils.getUserRole;


@Slf4j
@RequiredArgsConstructor
@Service
public class TransferRequestService {
    private static final Set<UserRole> COMPANY_USERS_PLUS_SUPER_ROLES = Stream.of(COMPANY_ADMIN, COMPANY_AGENT, ADMIN, SUPER_USER)
            .collect(Collectors.toSet());

    private final TransferRequestRepository transferRequestRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final TransferRequestMapper transferRequestMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final SortDataMapper sortDataMapper;
    private final KafkaSender kafkaSender;
    private final CompanyWebClient companyWebClient;


    @Transactional
    public TransferRqDto createTransferRq(CreateTransferRqDto createTransferRqDto, String clientEmail) {
        TransferRequestEntity creatingTransferRequest = transferRequestMapper.mapToEntity(createTransferRqDto);
        CompanyEntity fromCompanyEntity = companyRepository.findById(createTransferRqDto.getFromCompanyId())
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию с id: %s", createTransferRqDto.getFromCompanyId())));
        CompanyEntity toCompanyEntity = companyRepository.findById(createTransferRqDto.getToCompanyId())
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию с id: %s", createTransferRqDto.getToCompanyId())));
        UserEntity requestCreatorEntity = userRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти пользователя с email: %s", clientEmail)));

        creatingTransferRequest.setUser(requestCreatorEntity);
        creatingTransferRequest.setFromCompany(fromCompanyEntity);
        creatingTransferRequest.setToCompany(toCompanyEntity);
        creatingTransferRequest.setStatus(NEW);
        return transferRequestMapper.mapToTransferRqDto(transferRequestRepository.save(creatingTransferRequest));
    }

    @Transactional(readOnly = true)
    public TransferRqDto getTransferRqInfo(Long transferRequestId) {
        return transferRequestRepository.findById(transferRequestId).map(transferRequestMapper::mapToTransferRqDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти заявку на перенос по Id: %s", transferRequestId)));
    }


    @Transactional
    public TransferRqDto makeTransferRequestDecision(Long transferRequestId, TransferDecisionDto transferDecisionDto) {
        TransferRequestEntity transferRequest = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Не удалось найти заявку на перенос портфолио с Id: %s", transferRequestId)));

        switch (transferDecisionDto.getDecision()) {
            case ACCEPT -> {
                transferRequest.setStatus(DECISION_PROCESSING);
                CompanyEntity importingPortfolioCompany = Optional.ofNullable(transferRequest.getToCompany())
                        .orElseThrow(() -> new RuntimeException(
                                String.format("Не удалось найти компанию в которую будет выполняться перенос портфолио для transferId: %s", transferRequestId)));

                companyWebClient.triggerUserPortfolioExportEndPoint(importingPortfolioCompany.getTriggerUrlForExportUserPortfolio(), transferRequest.getFromProfileId());
            }
            case REJECT -> transferRequest.setStatus(CLOSED);
        }

        transferRequestMapper.mergeUpdateToTransferEntity(transferDecisionDto, transferRequest);
        return transferRequestMapper.mapToTransferRqDto(transferRequestRepository.save(transferRequest));
    }


    @Transactional(readOnly = true)
    public PageResponse<TransferRqDto> getTransferRqPage(TransferPageRequest transferPageRequest, UserDetails userDetails) {
        QPredicates predicateBuilder = predicateDataMapper.mapTransferFilterToQPredicates(transferPageRequest.getTransferFilter());

        //TODO: протестировать это обязательно (по моему нужны будут join в запросах, чтобы отрабатывали фильтры)
        UserRole userRole = getUserRole(userDetails);
        if (userRole == UserRole.CLIENT)
            predicateBuilder.add(userDetails.getUsername(), transferRequestEntity.user.email::eq);
        else if (COMPANY_USERS_PLUS_SUPER_ROLES.contains(userRole)) {
            Optional<String> userEmailOptional = Optional.ofNullable(userDetails)
                    .map(UserDetails::getUsername);
            CompanyEntity linkedCompanyEntity = userEmailOptional.flatMap(userRepository::findByEmail)
                    .map(UserEntity::getLinkedCompany)
                    .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти компанию связанную с email: %s",
                            userEmailOptional.orElse(null))));
            predicateBuilder.add(linkedCompanyEntity.getId(), transferRequestEntity.fromCompany.id::eq);
        }

        Sort transferRqPageSort = sortDataMapper.mapTransferRqSortSpecifiersToSpringSort(transferPageRequest.getSortOrderSpecifiers());
        PageRequest pageRequest = PageRequest.of(
                Optional.ofNullable(transferPageRequest.getPageNumber()).orElseThrow(),
                Optional.ofNullable(transferPageRequest.getPageSize()).orElseThrow(),
                transferRqPageSort);
        Page<TransferRequestEntity> transfersPage = transferRequestRepository.findAll(predicateBuilder.build(), pageRequest);

        return PageResponse.<TransferRqDto>builder()
                .data(transfersPage.getContent().stream().map(transferRequestMapper::mapToTransferRqDto)
                        .toList())
                .pageNumber(transfersPage.getNumber())
                .totalElements(transfersPage.getTotalElements())
                .totalPages(transfersPage.getTotalPages())
                .build();
    }


    @Transactional
    public void handleUserPortfolioEvent(UserPortfolioEvent userPortfolioEvent) {
        Long transferRequestId = userPortfolioEvent.getTransferRequestId();

        TransferRequestEntity transferRequest = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти заявку на перенос по Id: %s", transferRequestId)));

        String userProfileImportTopicName = Optional.ofNullable(transferRequest.getToCompany())
                .map(CompanyEntity::getUserProfileImportTopicName)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось определить топик для отправки портфолио по заявке на перенос с Id: %s", transferRequestId)));
        transferRequest.setStatus(CLOSED);
        transferRequestRepository.save(transferRequest);

        kafkaSender.sendUserPortfolio(userProfileImportTopicName, UserPortfolioEvent.builder()
                .transferRequestId(transferRequestId)
                .userPortfolio(userPortfolioEvent.getUserPortfolio())
                .profileId(transferRequest.getToProfileId())
                .build());
    }

}

