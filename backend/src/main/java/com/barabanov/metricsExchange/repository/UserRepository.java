package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends QuerydslPredicateExecutor<UserEntity>, CrudRepository<UserEntity, Long> {


    @Modifying
    @Query("""
            delete
            from UserEntity u
            where u.linkedCompany.id = :companyId
            """
    )
    void removeAllWith(Long companyId);
}
