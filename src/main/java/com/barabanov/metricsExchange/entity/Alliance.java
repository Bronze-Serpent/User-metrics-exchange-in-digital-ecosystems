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
@ToString(callSuper = true, exclude = "companies")
@EqualsAndHashCode(callSuper = true, exclude = "companies")
@Entity
public class Alliance extends AbstractEntity {

    private String name;

    private String description;

    @JoinTable(name = "alliance_company", joinColumns = {@JoinColumn(name = "alliance_id")},
            inverseJoinColumns = {@JoinColumn(name = "company_id")})
    @ManyToMany
    private Set<Company> companies;

    @OneToMany(mappedBy = "alliance")
    private Set<AlliancePoint> points;
}
