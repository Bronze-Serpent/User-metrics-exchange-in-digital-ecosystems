package com.barabanov.metricsExchange.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class TransferRequest extends AbstractEntity {

    private String fromProfileId;

    private String toProfileId;

    private String comment;

    private String decision;

    private String decisionComment;

    @JoinColumn(name = "from_company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Company fromCompany;

    @JoinColumn(name = "to_company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Company toCompany;
}
