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
@ToString(callSuper = true, exclude = {"company", "alliancePoints"})
@EqualsAndHashCode(callSuper = true, exclude = {"company", "alliancePoints"})
@Entity
public class CompanyPoint extends AbstractEntity {

    private String url;

    private String format;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @JoinColumn(name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    //TODO: тут не будет ли ловушки, проверить т.к. два раза JoinColumn в связт: а не MappedBy т.к. наборы у сущностей разные
    @JoinTable(name = "alliance_company_points",
            joinColumns = {@JoinColumn(name = "company_point_id")},
            inverseJoinColumns = {@JoinColumn(name = "alliance_point_id")})
    @ManyToMany
    private Set<AlliancePoint> alliancePoints;
}
