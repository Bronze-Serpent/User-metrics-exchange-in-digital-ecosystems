package com.barabanov.metricsExchange.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, exclude = {"companyUsers", "points"})
@EqualsAndHashCode(callSuper = true, exclude = {"companyUsers", "points"})
@Entity
@Table(name = "company")
public class CompanyEntity extends AbstractEntity {

    private String name;

    private String description;

    private Boolean suppUserProfileExchange;

    private String userProfileImportTopicName;

    private String triggerUrlForExportUserPortfolio;

    @OneToMany(mappedBy = "linkedCompany")
    private List<UserEntity> companyUsers;

    @OneToMany(mappedBy = "company")
    private List<CompanyPointEntity> points;

}
