package com.barabanov.metricsExchange.kafka;

import com.barabanov.metricsExchange.utils.DatabaseHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.RecoveryStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaCommonErrorHandler implements CommonErrorHandler {

    private final static long MSG_PROCESSING_ATTEMPTS = 3L;
    private final RecoveryStrategy recoveryStrategy = new FailedRecordTracker(null, new FixedBackOff(0, MSG_PROCESSING_ATTEMPTS - 1));
    private final DatabaseHelper databaseHelper;


    /**
     * Пропускать весь батч сообщений даже при бизнесовых ошибках видится слишком плохим вариантом. При обработке ошибок для батчей
     * весь батч будет повторно поставляться на обработку до тех пор, пока не обработается успешно, независимо от того есть ли подключение к БД или нет.
     * <p>
     * В логике сервиса должна быть предусмотрена обработка ошибки при работе с батчем. Например, обрабатывать батч по одному сообщению в случае возникновения ошибки.
     * А если возникает ошибка и по обработке по одному сообщению, то сохранять такое сообщение в отдельную таблицу, игнорировать или ещё как-либо обрабатывать.
     * В любом случае обрабатывать батч нужно полностью без ошибок, если нет проблем с инфраструктурой (например, с БД) и в таком случае можно выкидывать ошибку,
     * чтобы обработчик ещё раз поставил весь батч на обработку.
     */
    @Override
    public <K, V> ConsumerRecords<K, V> handleBatchAndReturnRemaining(Exception thrownException,
                                                                      ConsumerRecords<?, ?> data,
                                                                      Consumer<?, ?> consumer,
                                                                      MessageListenerContainer container,
                                                                      Runnable invokeListener) {

        log.error("При обработке батча сообщений из kafka из партиций {} произошла ошибка. Батч сообщений будет повторно передан обработчику", data.partitions());
        return (ConsumerRecords<K, V>) data;
    }


    @SneakyThrows
    @Override
    public boolean handleOne(Exception exception,
                             ConsumerRecord<?, ?> record,
                             Consumer<?, ?> consumer,
                             MessageListenerContainer container) {

        if (databaseHelper.isDatabaseAvailable()) {
            if (recoveryStrategy.recovered(record, exception, container, consumer)) {
                log.error("Было предпринято {} попыток обработки сообщения при наличии подключения к БД." +
                                " Offset будет увеличен! Текущие значения Topic: {} Partition: {} Offset: {}",
                        MSG_PROCESSING_ATTEMPTS, record.topic(), record.partition(), record.offset());
                consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset() + 1L);
                consumer.commitSync();
                return true;
            }
            log.error("Производятся повторные попытки обработки сообщения из кафки при наличии подключения к БД. Offset не будет увеличен." +
                            " Текущие значения Topic: {} Partition: {} Offset: {}",
                    record.topic(), record.partition(), record.offset());
            return false;
        }
        log.error("Во время обработки ошибки при чтении из кафка подключение к БД отсутствует! Offset не будет увеличен." +
                        " Текущие значения Topic: {} Partition: {} Offset: {}",
                record.topic(), record.partition(), record.offset());
        return false;
    }


    @Override
    public void handleOtherException(Exception exception, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        if (exception instanceof RecordDeserializationException ex) {
            TopicPartition topicPartition = ex.topicPartition();
            log.error("Ошибка десериализации при попытке считать сообщение из kafka. Offset будет увеличен!" +
                            " Текущие значения Topic: {} Partition: {} Offset: {}",
                    topicPartition.topic(), topicPartition.partition(), ex.offset());
            consumer.seek(topicPartition, ex.offset() + 1L);
            consumer.commitSync();
        } else
            log.error("Неожиданная ошибка при попытке считать сообщение из kafka.");
    }

}