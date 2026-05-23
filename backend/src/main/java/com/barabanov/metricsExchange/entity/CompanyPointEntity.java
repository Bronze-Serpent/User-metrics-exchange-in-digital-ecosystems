package com.barabanov.metricsExchange.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"company", "alliancePoint"})
@EqualsAndHashCode(callSuper = true, exclude = {"company", "alliancePoint"})
@Entity
@Table(name = "company_point")
public class CompanyPointEntity extends AbstractEntity {

    private String url;

    private String format;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @JoinColumn(name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CompanyEntity company;

    @JoinColumn(name = "alliance_point_id")
    @ManyToOne
    private AlliancePointEntity alliancePoint;

    public void setAlliancePoint(AlliancePointEntity alliancePoint) {
        if (alliancePoint != null)
            alliancePoint.getCompanyPoints().add(this);

        if (this.alliancePoint != null && !this.alliancePoint.equals(alliancePoint))
            this.alliancePoint.getCompanyPoints().remove(this);

        this.alliancePoint = alliancePoint;
    }
}
