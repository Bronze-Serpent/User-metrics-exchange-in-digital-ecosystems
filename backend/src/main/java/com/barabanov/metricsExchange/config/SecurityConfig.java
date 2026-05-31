package com.barabanov.metricsExchange.config;

import com.barabanov.metricsExchange.entity.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.stream.Stream;

import static com.barabanov.metricsExchange.entity.UserRole.*;
import static org.springframework.http.HttpMethod.DELETE;


@Configuration
public class SecurityConfig {

    private static final String[] ALL_EXCEPT_CLIENT_AUTHORITIES = Stream.of(COMPANY_ADMIN, COMPANY_AGENT, ADMIN, SUPER_USER)
            .map(UserRole::getAuthority)
            .toArray(String[]::new);


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("http://localhost:63342"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowedHeaders(List.of("*"));
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(urlAuthConfig -> urlAuthConfig
                                .requestMatchers("/user-exchange-metrics/user/register-client", "/user-exchange-metrics/companies", "/v3/api-docs/**", "/swagger-ui/**", "/user-exchange-metrics/alliance-points/**").permitAll()
//                                .requestMatchers("**/user/create").hasAnyAuthority(SUPER_USER.getAuthority(), COMPANY_AGENT.getAuthority())
//                                .requestMatchers(DELETE, "**/user/**").hasAnyAuthority(SUPER_USER.getAuthority(), COMPANY_AGENT.getAuthority())
//                                .requestMatchers("**/users").hasAnyAuthority(SUPER_USER.getAuthority(), COMPANY_AGENT.getAuthority())
//                                .requestMatchers("**/transfer-request/create").hasAnyAuthority(CLIENT.getAuthority())
//                                .requestMatchers("**/transfer-request").hasAnyAuthority(CLIENT.getAuthority())
//                                .requestMatchers("**/transfer-request/**").hasAnyAuthority(ALL_EXCEPT_CLIENT_AUTHORITIES)
//                                .requestMatchers("**/transfer-request/**/decision").hasAnyAuthority(SUPER_USER.getAuthority(), COMPANY_AGENT.getAuthority(), COMPANY_ADMIN.getAuthority())
//                                .requestMatchers("**/transfer-requests").hasAnyAuthority(SUPER_USER.getAuthority(), COMPANY_AGENT.getAuthority(), COMPANY_ADMIN.getAuthority())

                                // Если не будет автоматически открыт для preflight запрос
                                // (отправляется чтобы выяснить разрешает ли сервер кросс доменный запрос с такими методами и хэдерами).
                                // Для него не должно быть авторизации
//                              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("http://localhost:63342/frontend/index.html")
                        .loginProcessingUrl("/user-exchange-metrics/login")
                        // Чтобы не было редиректа, а просто отправились 200 и 400 статусы
                        .successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
                        .failureHandler((request, response, authentication) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                )
                .logout(logoutConfig -> logoutConfig
                        .logoutUrl("/user-exchange-metrics/logout")
                        .logoutSuccessUrl("http://localhost:63342/frontend/index.html"));

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
