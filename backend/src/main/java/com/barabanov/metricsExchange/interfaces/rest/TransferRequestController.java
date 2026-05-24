package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.service.TransferRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-exchange-metrics")
@RestController
public class TransferRequestController {

    private final TransferRequestService transferRequestService;


    // Рест для создания заявки на перенос рейтинга
    @PostMapping("/transfer-request/create")
    public TransferRqDto createTransferRq(@RequestBody CreateTransferRqDto createTransferRqDto) {

        log.info("Получен запрос на создание заявки");
        return transferRequestService.createTransferRq(createTransferRqDto);
    }


    // Рест получения информации по заявке
    @GetMapping("/transfer-request/{transferRequestId}")
    public TransferRqDto getTransferRqInfo(@PathVariable Long transferRequestId) {
        return transferRequestService.getTransferRqInfo(transferRequestId);
    }


    // Рест на вынесение решения по заявке
    @PostMapping("/transfer-request/{transferRequestId}/decision")
    public TransferRqDto makeTransferRqDecision(@PathVariable Long transferRequestId, @RequestBody TransferDecisionDto transferDecisionDto) {
        return transferRequestService.makeTransferRequestDecision(transferRequestId, transferDecisionDto);
    }


    // Рест получения информации по заявкам пользователя с пагинацией (Рест на получения списка заявок для компании с пагинацией (тот же рест, но с другим фильтром)) + Он же рест на получение открытых заявок на компанию. Просто разные фильтры
    @PostMapping("/transfer-requests")
    public PageResponse<TransferRqDto> getTransferRequestsPage(@RequestBody TransferPageRequest transferPageRequest) {

        log.info("Получен запрос на получение набора заявок на перенос");
        return transferRequestService.getTransferRqPage(transferPageRequest);
    }


}
