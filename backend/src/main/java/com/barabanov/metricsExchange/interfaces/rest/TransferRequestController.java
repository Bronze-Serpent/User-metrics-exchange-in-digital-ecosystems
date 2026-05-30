package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.service.TransferRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-exchange-metrics")
@RestController
public class TransferRequestController {

    private final TransferRequestService transferRequestService;


    // Рест для создания заявки на перенос рейтинга
    @PostMapping("/transfer-request/create")
    public TransferRqDto createTransferRq(@RequestBody CreateTransferRqDto createTransferRqDto,
                                          @AuthenticationPrincipal UserDetails clientDetails) {

        log.info("Получен запрос на создание заявки от пользователя с username: {}", clientDetails.getUsername());
        return transferRequestService.createTransferRq(createTransferRqDto, clientDetails.getUsername());
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


    // Рест получения информации по заявкам пользователя с пагинацией
    // Рест на получения списка заявок для компании с пагинацией
    @PostMapping("/transfer-requests")
    public PageResponse<TransferRqDto> getTransferRequestsPage(@RequestBody TransferPageRequest transferPageRequest,
                                                               @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Получен запрос на получение набора заявок на перенос");
        return transferRequestService.getTransferRqPage(transferPageRequest, userDetails);
    }


}
