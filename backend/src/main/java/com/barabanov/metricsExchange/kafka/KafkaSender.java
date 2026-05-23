package com.barabanov.metricsExchange.kafka;

import com.barabanov.metricsExchange.kafka.dto.UserPortfolioEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.barabanov.metricsExchange.utils.DataExtractionUtils.getTransferRequestId;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    //Отправка партнёру портфолио
    public void sendUserPortfolio(String topicName, UserPortfolioEvent userPortfolioEvent) {
        Long transferRequestId = getTransferRequestId(userPortfolioEvent);
        log.info("Отправляется портфолио по заявке с transferRequestId: {}, в топик с именем: {}",
                transferRequestId, topicName);

        kafkaTemplate.send(topicName,
                        userPortfolioEvent)
                .whenCompleteAsync(((stringObjectSendResult, throwable) -> {
                    if (throwable == null)
                        log.info("Портфолио по заявке с transferRequestId: {} успешно отправлено.", transferRequestId);
                    else
                        log.error("При отправке портволио по заявке с transferRequestId: {} возникла ошибка!", transferRequestId, throwable);
                }));
    }
}
