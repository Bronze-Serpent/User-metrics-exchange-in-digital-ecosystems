package com.barabanov.metricsExchange.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class Alliance extends AbstractEntity {

    private String name;

    private String description;

    @JoinTable(name = "alliance_company", joinColumns = {@JoinColumn(name = "alliance_id")},
            inverseJoinColumns = {@JoinColumn(name = "company_id")})
    @ManyToMany
    private Set<Company> companies;
}
