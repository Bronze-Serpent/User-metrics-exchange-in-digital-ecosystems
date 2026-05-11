package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointCreateDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.AlliancePointPageRequest;
import com.barabanov.metricsExchange.interfaces.rest.dto.PageResponse;
import com.barabanov.metricsExchange.service.AlliancePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


// TODO: поправить в диаграммах Происходит не совсем добавление компании в альянс, а скорее подключение к одному из поинтов альянса
//TODO:продумать как как обращаться к поинтам и получать из них инфу. Мб не по отдельному url, а вставить в путь айди точки альянса
@Slf4j
@RequestMapping("/alliance-exchange-metrics")
@RequiredArgsConstructor
@RestController
public class AlliancePointController {

    private final AlliancePointService alliancePointService;


    // Рест на создание новой связи (точки) в альянсе
    @PostMapping("/alliance-point/create")
    public AlliancePointDto createAlliancePoint(AlliancePointCreateDto alliancePointCreateDto) {
        return alliancePointService.createAlliancePoint(alliancePointCreateDto);
    }


    // Рест на получение информации о точке в альянсе
    @PostMapping("/alliance-point/{alliancePointId}")
    public AlliancePointDto createAlliancePoint(@PathVariable Long alliancePointId) {
        return alliancePointService.getAlliancePointInfo(alliancePointId);
    }


    // Рест на удаления связи (точки) в альянсе
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/alliance-point/{alliancePointId}")
    public void deleteAlliancePoint(@PathVariable Long alliancePointId) {
        alliancePointService.deleteAlliancePointInfo(alliancePointId);
    }


    // Рест на просмотре всех точек с фильтрацией (по allianceId минимум)
    // Рест на удаления связи (точки) в альянсе
    @PostMapping("/alliance-points")
    public PageResponse<AlliancePointDto> deleteAlliancePoint(AlliancePointPageRequest alliancePointPageRequest) {
        return alliancePointService.getAlliancePointsPage(alliancePointPageRequest);
    }


    // Рест на добавление точки компании в точку альянса
    @PostMapping("/alliance-point/{alliancePointId}/add-company-point")
    public AlliancePointDto addCompanyPointInAlliancePoint(@PathVariable Long alliancePointId, @RequestParam Long companyPointId) {
        return alliancePointService.addCompanyPointFor(alliancePointId, companyPointId);
    }


    // Рест на удаление точки компании из точки альянса
    @PostMapping("/alliance-point/{alliancePointId}/delete-company-point")
    public AlliancePointDto deleteCompanyPointFromAlliancePoint(@PathVariable Long alliancePointId, @RequestParam Long companyPointId) {
        return alliancePointService.deleteCompanyPointFrom(alliancePointId, companyPointId);
    }


    // Рест на получение данных из альянса
    @GetMapping("/alliance-point/{alliancePointId}/metrics")
    public void getUserMetrics(@PathVariable Long alliancePointId) {
        // TODO: реализовать
    }


}
