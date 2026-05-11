package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.UserEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends QuerydslPredicateExecutor<UserEntity>, CrudRepository<UserEntity, Long> {
}
