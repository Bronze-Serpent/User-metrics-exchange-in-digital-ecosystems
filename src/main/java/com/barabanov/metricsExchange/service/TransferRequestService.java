package com.barabanov.metricsExchange.service;

import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import com.barabanov.metricsExchange.interfaces.rest.dto.*;
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

    @Transactional
    public TransferRqDto makeTransferRequestDecision(Long transferRequestId, TransferDecision transferDecision) {
        TransferRequestEntity transferRequestEntity = transferRequestRepository.findById(transferRequestId)
                .orElseThrow(() -> new RuntimeException(String.format("Не удалось найти заявку на перенос по Id: %s", transferRequestId)));

        transferRequestEntity.setComment(transferDecision.getComment());
        transferRequestEntity.setDecision(transferDecision.getDecisionType());
        return transferRequestMapper.mapToTransferRqDto(transferRequestRepository.save(transferRequestEntity));
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

    public void handleUserPortfolioEvent(UserPortfolioEvent userPortfolioEvent) {

    }
}

