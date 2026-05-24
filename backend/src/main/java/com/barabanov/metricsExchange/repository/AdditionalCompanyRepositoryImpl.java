package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.CompanyEntity;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.barabanov.metricsExchange.entity.QCompanyEntity.companyEntity;


@Repository
@RequiredArgsConstructor
public class AdditionalCompanyRepositoryImpl implements AdditionalCompanyRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<CompanyEntity> findCompaniesWith(Predicate predicate, Integer limit, Integer offset) {

        return new JPAQuery<CompanyEntity>(entityManager)
                .select(companyEntity)
                .from(companyEntity)
                .where(predicate)
                .limit(limit)
                .offset(offset)
                .fetch();
    }
}
