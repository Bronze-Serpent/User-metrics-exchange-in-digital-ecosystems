package com.barabanov.metricsExchange.utils;

import com.barabanov.metricsExchange.entity.UserRole;
import com.barabanov.metricsExchange.kafka.dto.UserPortfolioEvent;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Iterator;
import java.util.Optional;


@NoArgsConstructor
public class DataExtractionUtils {


    public static Long getTransferRequestId(UserPortfolioEvent userPortfolioEvent) {
        return Optional.ofNullable(userPortfolioEvent)
                .map(UserPortfolioEvent::getTransferRequestId)
                .orElse(null);
    }

    public static UserRole getUserRole(UserDetails userDetails) {
        return Optional.ofNullable(userDetails)
                .map(UserDetails::getAuthorities)
                .map(authorities -> {
                    Iterator<? extends GrantedAuthority> authoritiesIterator = authorities.iterator();
                    return authoritiesIterator.hasNext()
                            ? authoritiesIterator.next()
                            : null;
                })
                .map(GrantedAuthority::getAuthority)
                .map(UserRole::valueOf)
                .orElse(null);
    }
}
