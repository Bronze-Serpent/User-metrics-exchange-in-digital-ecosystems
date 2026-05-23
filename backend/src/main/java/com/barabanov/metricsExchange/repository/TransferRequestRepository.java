package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TransferRequestRepository extends CrudRepository<TransferRequestEntity, Long>,
        QuerydslPredicateExecutor<TransferRequestEntity> {

}
