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
@ToString(callSuper = true, exclude = {"company", "alliancePoint"})
@EqualsAndHashCode(callSuper = true, exclude = {"company", "alliancePoint"})
@Entity
@Table(name = "company_point")
public class CompanyPointEntity extends AbstractEntity {

    /**
     * Можно хранить host в Company, а path в CompanyPoint, но тогда придётся получать информацию и о компании при запросе метрик.
     * Чтобы этого не делать хранится всё вместе
     *
     * TODO: рассмотреть вариант разнесения этого и перехода на нативный запрос при получении метрик + возможно, кэш этого запроса
     */
    private String url;

    private String format;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    @JoinColumn(name = "company_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CompanyEntity company;

    @JoinColumn(name = "alliance_point_id")
    @ManyToOne
    private AlliancePointEntity alliancePoint;


    //TODO: а так можно делать? У новой сущности это поле будет не null, да. А при запросе из БД у хибера? Хотя из-за контракта прокси думаю можно
    public void setAlliancePoint(AlliancePointEntity alliancePoint) {
        if (alliancePoint != null)
            alliancePoint.getCompanyPoints().add(this);

        if (this.alliancePoint != null && !this.alliancePoint.equals(alliancePoint))
            this.alliancePoint.getCompanyPoints().remove(this);

        this.alliancePoint = alliancePoint;
    }
}
