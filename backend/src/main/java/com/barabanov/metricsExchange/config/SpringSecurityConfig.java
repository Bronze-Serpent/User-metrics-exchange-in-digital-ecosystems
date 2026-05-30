package com.barabanov.metricsExchange.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;


@Configuration
public class SpringSecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("*"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowedHeaders(List.of("*"));
                    return configuration;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .formLogin(form -> form
//                                .loginPage("/login") // Custom HTML login page mapping
//                        .loginProcessingUrl("/perform_login") // URL where the form submits credentials
//                                .usernameParameter("email") // Custom input name for username
//                                .passwordParameter("pass") // Custom input name for password
                                .defaultSuccessUrl("/dashboard", true) // Redirect here after successful login
//                        .failureUrl("/login?error=true") // Redirect here on failed login
                                .permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
