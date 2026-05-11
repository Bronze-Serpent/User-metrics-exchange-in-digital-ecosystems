package com.barabanov.metricsExchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"fromCompany", "toCompany"})
@EqualsAndHashCode(callSuper = true, exclude = {"fromCompany", "toCompany"})
@Entity
@Table(name = "transfer_request")
public class TransferRequestEntity extends AbstractEntity {

    private String fromProfileId;

    private String toProfileId;

    private String comment;

    @Enumerated(EnumType.STRING)
    private TransferDecisionType decision;

    private String decisionComment;

    @JoinColumn(name = "from_company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CompanyEntity fromCompany;

    @JoinColumn(name = "to_company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CompanyEntity toCompany;
}
