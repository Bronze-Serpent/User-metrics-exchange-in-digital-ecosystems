package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.service.AllianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-exchange-metrics")
@RestController
public class AllianceController {

    private final AllianceService allianceService;


    // Рест на создание альянса
    @PostMapping("/alliance/create")
    public AllianceDto createAlliance(@RequestBody CreateAllianceDto createAllianceDto) {

        log.info("Получен запрос на создание альянса");
        return allianceService.createAlliance(createAllianceDto);
    }


    // Рест на просмотр альянса
    @GetMapping("/alliance/{allianceId}")
    public AllianceDto getAllianceInfo(@PathVariable Long allianceId) {

        log.info("Получен запрос на получение информации об альянсе");
        return allianceService.getAllianceInfo(allianceId);
    }


    // Рест на удаление альянса
    @DeleteMapping("/alliance/{allianceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlliance(@PathVariable Long allianceId) {

        log.info("Получен запрос на удаление альянса");
        allianceService.deleteAlliance(allianceId);
    }


    // Рест на изменение названия / описания альянса
    @PutMapping("/alliance/{allianceId}")
    public AllianceDto updateAlliance(@PathVariable Long allianceId, AllianceUpdateDto allianceUpdateDto) {

        log.info("Получен запрос на обновление альянса");
        return allianceService.updateAlliance(allianceId, allianceUpdateDto);
    }


    // Рест на просмотр всех альянсов с пагинацией
    @PostMapping("/alliances")
    public PageResponse<AllianceDto> getAlliances(@RequestBody AlliancePageRequest alliancePageRequest) {

        log.info("Получен запрос на получение набора альянсов");
        return allianceService.getAlliancePage(alliancePageRequest);
    }

}
