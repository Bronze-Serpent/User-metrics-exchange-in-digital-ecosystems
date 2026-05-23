package com.barabanov.metricsExchange.kafka;

import com.barabanov.metricsExchange.kafka.dto.UserPortfolioEvent;
import com.barabanov.metricsExchange.service.TransferRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.barabanov.metricsExchange.kafka.KafkaConfiguration.CONTAINER_POST_PROCESSOR_COMMON_ERROR_HANDLER_BEAN_NAME;
import static com.barabanov.metricsExchange.utils.DataExtractionUtils.getTransferRequestId;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final TransferRequestService transferRequestService;


    // CLIENT_PORTFOLIO_EVENT.V1
    @KafkaListener(topics = "${kafka-topics.consumer.client-portfolio-topic-name}",
            properties = "spring.json.value.default.type=com.barabanov.metricsExchange.kafka.dto.UserPortfolioEvent",
            containerPostProcessor = CONTAINER_POST_PROCESSOR_COMMON_ERROR_HANDLER_BEAN_NAME,
            concurrency = "${kafka-topics.consumer.client-portfolio-topic-concurrency}")
    public void listenUserPortfolioEvent(UserPortfolioEvent userPortfolioEvent) {
        log.info("Получено сообщение с портфолио по заявке о переносе профиля с id: {}", getTransferRequestId(userPortfolioEvent));

        transferRequestService.handleUserPortfolioEvent(userPortfolioEvent);
    }

}
