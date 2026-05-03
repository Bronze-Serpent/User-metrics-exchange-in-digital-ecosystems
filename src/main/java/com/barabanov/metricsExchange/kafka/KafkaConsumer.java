package com.barabanov.metricsExchange.kafka;

import com.barabanov.metricsExchange.kafka.dto.UserPortfolio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.barabanov.metricsExchange.kafka.KafkaConfiguration.CONTAINER_POST_PROCESSOR_COMMON_ERROR_HANDLER_BEAN_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {


    // CLIENT_PORTFOLIO_EVENT.V1
    @KafkaListener(topics = "kafka-topics.consumer.client-portfolio-topic-name",
            properties = "spring.json.value.default.type=com.barabanov.metricsExchange.kafka.dto.CustomerReview",
            containerPostProcessor = CONTAINER_POST_PROCESSOR_COMMON_ERROR_HANDLER_BEAN_NAME,
            concurrency = "kafka-topics.consumer.client-portfolio-topic-concurrency")
    public void listenUserPortfolioMsgEvent(UserPortfolio userPortfolio) {

    }

}
