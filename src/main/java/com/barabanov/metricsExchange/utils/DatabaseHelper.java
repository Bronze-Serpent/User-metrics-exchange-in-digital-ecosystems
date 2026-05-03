package com.barabanov.metricsExchange.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@RequiredArgsConstructor
@Component
public class DatabaseHelper {

    private final DataSource dataSource;

    /**
     * Можно было бы использовать dataSource.getConnection().isValid(), но это не совсем корректно.
     * В datasource подключения кэшируются и не создаются / закрываются при каждом обращении.
     * Соответственно может получиться такой кейс:
     * БД отвалилась, БД восстановилась, в пуле подключений до сих пор лежат битые подключения и
     * dataSource.getConnection().isValid() будет возвращать false пока пул не проверит эти подключения или не выбросит их по таймауту
     * т.е. может возвращаться false даже после того как БД восстановится.
     * <p>
     * Вроде бы dataSource.getConnection().isValid() может проверять подключение без запроса к БД -> является менее надёжным, но более быстрым.
     * <p>
     * Кстати в разных пулах соединений по разному настроены таймауты жизни подключений.
     * В HikariCP по умолчанию соединения живут пол часа. В Tomcat JDBC Pool и Apache Commons DBCP2 соединения живут бесконечно.
     * <p>
     * Т.е. код опирающийся на isValid может повиснуть надолго / навсегда.
     * Прямой запрос к БД в качестве проверки надёжнее, но почему это решает проблему работы с битыми соединениями в пуле в теории я так и не понял.
     * На моей практике с isValid() приложение иногда продолжало думать что БД недоступна когда она уже восстановилась.
     * С другой стороны обычные запросы к БД начинали проходить практически сразу, как только БД восстановилась.
     * -> отдано предпочтение обычному запросу.
     */
    public boolean isDatabaseAvailable() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("SELECT 1");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
