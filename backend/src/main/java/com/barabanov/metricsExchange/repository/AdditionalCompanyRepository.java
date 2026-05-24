package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.querydsl.core.types.Predicate;

import java.util.List;


public interface AdditionalCompanyRepository {

    List<CompanyEntity> findCompaniesWith(Predicate predicate, Integer limit, Integer offset);
}
