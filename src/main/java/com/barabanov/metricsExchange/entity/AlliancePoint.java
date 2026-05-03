package com.barabanov.metricsExchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"alliance", "companyPoints"})
@EqualsAndHashCode(callSuper = true, exclude = {"alliance", "companyPoints"})
@Entity
public class AlliancePoint extends AbstractEntity {

    private String format;

    private String url;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alliance_id")
    private Alliance alliance;

    @JoinTable(name = "alliance_company_points",
            joinColumns = {@JoinColumn(name = "alliance_point_id")},
            inverseJoinColumns = {@JoinColumn(name = "company_point_id")})
    @ManyToMany
    private Set<CompanyPoint> companyPoints;
}
