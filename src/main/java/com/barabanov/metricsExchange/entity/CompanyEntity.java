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
@ToString(callSuper = true, exclude = {"owner", "points"})
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "points"})
@Entity
@Table(name = "company")
public class CompanyEntity extends AbstractEntity {

    private String name;

    private String description;

    @JoinColumn(name = "owner_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity owner;

    @OneToMany(mappedBy = "company")
    private List<CompanyPointEntity> points;

}
