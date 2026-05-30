package com.barabanov.metricsExchange.config;

import org.springframework.context.annotation.Configuration;


@Configuration
public class CorsConfig {


    // Перестаёт работать после подключения spring-security т.к. там CSRF Это фильтр в другом другом объекте, полагаю
/*
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedOrigins("http://localhost:63342")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }*/
}
