package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.service.CompanyPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-exchange-metrics")
@RestController
public class CompanyPointController {

    private final CompanyPointService companyPointService;


    // Рест на создание у компании точку для предоставления метрик
    @PostMapping("/company-point/create")
    public CompanyPointDto createCompanyPoint(@RequestBody CreateCompanyPointDto createCompanyPointDto,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Получен запрос на создание точки компании");
        return companyPointService.createCompanyPoint(createCompanyPointDto, userDetails.getUsername());
    }


    // Рест для получения информации о точки компании для предоставления метрик
    @GetMapping("/company-point/{companyPointId}")
    public CompanyPointDto createCompanyPointInfo(@PathVariable Long companyPointId) {

        log.info("Получен запрос для получения информации о точке компании");
        return companyPointService.getCompanyPointInfo(companyPointId);
    }


    // Рест на удаление у компании точки для предоставления метрик (только в случае если она не используется ни в одной точке альянса)
    @DeleteMapping("/company-point/{companyPointId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompanyPoint(@PathVariable Long companyPointId) {

        log.info("Получен запрос на удаление точки компании");
        companyPointService.deleteCompanyPoint(companyPointId);
    }


    // Рест на просмотр списка всех точек компании для предоставления метрик с пагинацией
    // Рест на просмотр списка точек компаний для конкретной точки альянса
    @PostMapping("/company-points")
    public PageResponse<CompanyPointDto> getPointsPage(@RequestBody CompanyPointPageRequest companyPointPageRequest,
                                                       @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Получен запрос на получение набора точек компаний");
        return companyPointService.getCompanyPointPage(companyPointPageRequest, userDetails);
    }


    // Рест на обновление статуса компании
    @PutMapping("/company-points/{companyPointId}")
    public CompanyPointDto updateCompanyPoint(@PathVariable Long companyPointId, CompanyPointUpdateDto companyPointUpdateDto) {

        log.info("Получен запрос на обновление точки компании");
        return companyPointService.updateCompanyPoint(companyPointId, companyPointUpdateDto);
    }

}
