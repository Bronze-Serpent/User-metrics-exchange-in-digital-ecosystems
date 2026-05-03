package com.barabanov.metricsExchange.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class AlliancePoint extends AbstractEntity {

    private String format;

    private String url;

    @Enumerated(EnumType.STRING)
    private PointStatus status;
}
