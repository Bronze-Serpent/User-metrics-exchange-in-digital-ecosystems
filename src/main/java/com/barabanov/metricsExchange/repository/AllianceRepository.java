package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.AllianceEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface AllianceRepository extends CrudRepository<AllianceEntity, Long>, QuerydslPredicateExecutor<AllianceEntity> {
}
