package com.barabanov.metricsExchange.interfaces.rest;

import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.CompanyPageRequest;
import com.barabanov.metricsExchange.interfaces.rest.dto.CreateCompanyDto;
import com.barabanov.metricsExchange.interfaces.rest.dto.PageResponse;
import com.barabanov.metricsExchange.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Company-exchange-metrics")
@RestController
public class CompanyController {

    private final CompanyService companyService;


    // Рест на создание новой компании в системе
    @PostMapping("/company/create")
    public CompanyDto createCompany(CreateCompanyDto createCompanyDto) {

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

    // Рест на получение списка компаний партнёров, с пагинацией (возможно: в будущем сделать у компании признак того участвует ли она в переносе профилей или только в альянсах состоит)
    @PostMapping("/companies")
    public PageResponse<CompanyDto> getCompanies(CompanyPageRequest CompanyPageRequest) {

        log.info("Получен запрос на получение набора компаний");
        return companyService.getCompanyPage(CompanyPageRequest);
    }

}
