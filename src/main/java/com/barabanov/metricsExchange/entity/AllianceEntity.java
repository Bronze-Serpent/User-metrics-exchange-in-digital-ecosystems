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
@ToString(callSuper = true, exclude = "points")
@EqualsAndHashCode(callSuper = true, exclude = "points")
@Entity
@Table(name = "alliance")
public class AllianceEntity extends AbstractEntity {

    private String name;

    private String description;

    @OneToMany(mappedBy = "alliance")
    private List<AlliancePointEntity> points;
}
