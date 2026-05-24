package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.*;
import com.barabanov.metricsExchange.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user-exchange-metrics")
@RestController
public class CompanyController {

    private final CompanyService companyService;


    // Рест на создание новой компании в системе
    @PostMapping("/company/create")
    public CompanyDto createCompany(@RequestBody CreateCompanyDto createCompanyDto) {

        log.info("Получен запрос на создание компании");
        return companyService.createCompany(createCompanyDto);
    }


    @GetMapping("/company/{companyId}")
    public CompanyDto getCompany(@PathVariable Long companyId) {

        log.info("Получен запрос на получение информации по компании");
        return companyService.getCompanyInfo(companyId);
    }


    // Рест на удаление компании
    @DeleteMapping("/company/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable Long companyId) {

        log.info("Получен запрос на удаление компании");
        companyService.deleteCompany(companyId);
    }


    // Рест на получение списка компаний партнёров, с пагинацией
    @PostMapping("/companies")
    public PageResponse<CompanyDto> getCompanies(@RequestBody CompanyPageRequest companyPageRequest) {

        log.info("Получен запрос на получение страницы компаний");
        return companyService.getCompanyPage(companyPageRequest);
    }


    // Рест на получение значений для списков компаний
    @GetMapping("/companies/id-name-summary")
    public List<CompanyIdNameDto> getCompaniesIaNameSummary(Boolean suppUserProfileExchange) {

        log.info("Получен запрос на получение краткой информации о компаниях");
        return companyService.getIdNameSummary(suppUserProfileExchange);
    }

}
