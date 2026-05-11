package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.CompanyPointEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface CompanyPointRepository extends QuerydslPredicateExecutor<CompanyPointEntity>, CrudRepository<CompanyPointEntity, Long> {
}
