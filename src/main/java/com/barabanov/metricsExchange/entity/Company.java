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
@ToString(callSuper = true, exclude = {"owner", "alliances", "points"})
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "alliances", "points"})
@Entity
public class Company extends AbstractEntity {

    private String name;

    private String description;

    @JoinColumn(name = "owner_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User owner;

    @OneToMany(mappedBy = "company")
    private Set<CompanyPoint> points;

    @ManyToMany(mappedBy = "companies")
    private Set<Alliance> alliances;
}
