package com.barabanov.metricsExchange.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ContainerPostProcessor;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.CommonErrorHandler;


@Configuration
public class KafkaConfiguration {

    public static final String CONTAINER_POST_PROCESSOR_COMMON_ERROR_HANDLER_BEAN_NAME = "commonErrorHandlerContainerPostProcessor";


    @Bean(CONTAINER_POST_PROCESSOR_COMMON_ERROR_HANDLER_BEAN_NAME)
    public ContainerPostProcessor<String, Object, AbstractMessageListenerContainer<String, Object>> commonErrorHandlerContainerPostProcessor(
            CommonErrorHandler commonErrorHandler) {
        return container -> container.setCommonErrorHandler(commonErrorHandler);
    }

}
