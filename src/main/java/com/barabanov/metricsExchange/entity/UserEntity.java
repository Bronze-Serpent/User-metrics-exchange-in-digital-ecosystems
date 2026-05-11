package com.barabanov.metricsExchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = "linkedCompany")
@EqualsAndHashCode(callSuper = true, exclude = "linkedCompany")
@Entity
@Table(name = "user")
public class UserEntity extends AbstractEntity {

    private String passwordHash;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    private CompanyEntity linkedCompany;
}
