package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface CompanyRepository extends QuerydslPredicateExecutor<CompanyEntity>, CrudRepository<CompanyEntity, Long>,
        AdditionalCompanyRepository {
}
