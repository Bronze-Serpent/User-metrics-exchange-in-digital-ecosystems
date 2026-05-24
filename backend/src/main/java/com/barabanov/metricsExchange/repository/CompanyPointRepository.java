package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.CompanyPointEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;


public interface CompanyPointRepository extends QuerydslPredicateExecutor<CompanyPointEntity>, CrudRepository<CompanyPointEntity, Long> {

    @Modifying
    @Query("""
            delete
            from CompanyPointEntity cp
            where cp.company.id = :companyId
            """)
    void removeAllWith(Long companyId);
}
