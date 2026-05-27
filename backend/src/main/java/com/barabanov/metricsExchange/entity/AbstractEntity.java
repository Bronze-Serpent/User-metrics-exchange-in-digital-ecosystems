package com.barabanov.metricsExchange.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;


    /**
     * @CreatedDate не поддерживает OffsetDateTime. Local, Instant, Date, timestamp.
     * Возможно, стоит на backend всегда использовать Instant, но бывают случаи когда нужно знать часовой пояс пользователя
     * (например, если нужно ответить пользователю о том что заявка будет разрешена не позднее <даты>, где датой должно быть время до 12 часов след. рабочего дня)
     */
    @PrePersist
    public void fillFieldsPrePersist() {
        OffsetDateTime nowDateTime = OffsetDateTime.now();
        createdAt = nowDateTime;
        updatedAt = nowDateTime;
    }

    @PreUpdate
    public void fillFieldsPreUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
