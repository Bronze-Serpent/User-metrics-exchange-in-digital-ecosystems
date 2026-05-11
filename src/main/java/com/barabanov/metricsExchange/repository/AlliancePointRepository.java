package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.AlliancePointEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface AlliancePointRepository extends CrudRepository<AlliancePointEntity, Long>,
        QuerydslPredicateExecutor<AlliancePointEntity> {
}
