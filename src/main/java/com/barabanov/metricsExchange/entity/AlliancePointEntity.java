package com.barabanov.metricsExchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"alliance", "companyPoints"})
@EqualsAndHashCode(callSuper = true, exclude = {"alliance", "companyPoints"})
@Entity
@Table(name = "alliance_point")
public class AlliancePointEntity extends AbstractEntity {

    private String format;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alliance_id")
    private AllianceEntity alliance;

    //TODO: можно ли так указывать по сути дефолтное значени? Для новой сущности так буде работать, да. А для сущности которую достали из БД?
    @Builder.Default
    @OneToMany(mappedBy = "alliancePoint")
    private Set<CompanyPointEntity> companyPoints = new HashSet<>();
}
