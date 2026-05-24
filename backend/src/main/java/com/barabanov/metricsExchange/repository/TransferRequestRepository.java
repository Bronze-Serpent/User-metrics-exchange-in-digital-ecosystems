package com.barabanov.metricsExchange.repository;

import com.barabanov.metricsExchange.entity.TransferDecision;
import com.barabanov.metricsExchange.entity.TransferRequestEntity;
import com.barabanov.metricsExchange.entity.TransferStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TransferRequestRepository extends CrudRepository<TransferRequestEntity, Long>,
        QuerydslPredicateExecutor<TransferRequestEntity> {


    @Modifying
    @Query("""
            update TransferRequestEntity t
            set t.decision = :transferDecision,
                t.status = :transferStatus,
                t.comment = :comment
            where t.fromCompany.id = :companyId or t.toCompany.id = :companyId
            """)
    void setDecisionStatusCommentAllWith(TransferDecision transferDecision, TransferStatus transferStatus, String comment, Long companyId);
}
