package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.kafka.KafkaSender;
import com.barabanov.metricsExchange.kafka.dto.UserPortfolioEvent;
import com.barabanov.metricsExchange.mapper.PredicateDataMapper;
import com.barabanov.metricsExchange.mapper.TransferRequestMapper;
import com.barabanov.metricsExchange.repository.TransferRequestRepository;
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
public class TransferRequestService {

    private final TransferRequestRepository transferRequestRepository;
    private final TransferRequestMapper transferRequestMapper;
    private final PredicateDataMapper predicateDataMapper;
    private final KafkaSender kafkaSender;
    private final CompanyWebService companyWebService;


    @Transactional
    public TransferRqDto createTransferRq(CreateTransferRqDto createTransferRqDto) {
        TransferRequestEntity creatingTransferRequest = transferRequestMapper.mapToEntity(createTransferRqDto);
        return transferRequestMapper.mapToTransferRqDto(transferRequestRepository.save(creatingTransferRequest));
    }

    @Transactional(readOnly = true)
    public TransferRqDto getTransferRqInfo(Long transferRequestId) {
        return transferRequestRepository.findById(transferRequestId).map(transferRequestMapper::mapToTransferRqDto)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти заявку на перенос по Id: %s", transferRequestId)));
    }


    @Transactional //TODO: сделать transactionalOutbox для триггера выгрузки
    public TransferRqDto makeTransferRequestDecision(Long transferRequestId, TransferDecision transferDecision) {
        TransferRequestEntity transferRequest = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Не удалось найти заявку на перенос портфолио с Id: %s", transferRequestId)));
        CompanyEntity importingPortfolioCompany = Optional.ofNullable(transferRequest.getToCompany())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Не удалось найти компанию в которую будет выполняться перенос портфолио для transferId: %s", transferRequestId)));

        companyWebService.triggerUserPortfolioExportEndPoint(importingPortfolioCompany.getTriggerUrlForExportUserPortfolio(), transferRequest.getFromProfileId());

        transferRequest.setComment(transferDecision.getComment());
        transferRequest.setDecision(transferDecision.getDecisionType());

        return transferRequestMapper.mapToTransferRqDto(transferRequestRepository.save(transferRequest));
    }


    @Transactional(readOnly = true)
    public PageResponse<TransferRqDto> getTransferRqPage(TransferPageRequest transferPageRequest) {
        Predicate predicate = predicateDataMapper.mapTransferFilterToPredicate(transferPageRequest.getTransferFilter());

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(transferPageRequest.getPageNumber())
                .orElseThrow(), Optional.ofNullable(transferPageRequest.getPageSize())
                .orElseThrow());
        Page<TransferRequestEntity> transfersPage = transferRequestRepository.findAll(predicate, pageRequest);

        return PageResponse.<TransferRqDto>builder()
                .data(transfersPage.getContent().stream().map(transferRequestMapper::mapToTransferRqDto)
                        .toList())
                .pageNumber(transfersPage.getNumber())
                .totalElements(transfersPage.getTotalElements())
                .totalPages(transfersPage.getTotalPages())
                .build();
    }


    // TODO: Сделать всё же решения и статусы по заявкам. Тут можно было бы двигать статус, а решение уже конечное, ACCPT
    //  + нехвататет transactional outbox если появится взаимодействие с БД.
    //  Если же БД не будет необходимо продумать логику коммита оффсета только в случае успешной отправки в kafka
    public void handleUserPortfolioEvent(UserPortfolioEvent userPortfolioEvent) {
        Long transferRequestId = userPortfolioEvent.getTransferRequestId();

        TransferRequestEntity transferRequest = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти заявку на перенос по Id: %s", transferRequestId)));

        String userProfileImportTopicName = Optional.ofNullable(transferRequest.getToCompany())
                .map(CompanyEntity::getUserProfileImportTopicName)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось определить топик для отправки портфолио по заявке на перенос с Id: %s", transferRequestId)));

        kafkaSender.sendUserPortfolio(userProfileImportTopicName, UserPortfolioEvent.builder()
                .transferRequestId(transferRequestId)
                .userPortfolio(userPortfolioEvent.getUserPortfolio())
                .profileId(transferRequest.getToProfileId())
                .build());
    }
}

