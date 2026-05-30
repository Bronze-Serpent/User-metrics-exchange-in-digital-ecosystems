package com.barabanov.metricsExchange.entity;

import org.springframework.security.core.GrantedAuthority;


public enum UserRole implements GrantedAuthority {
    CLIENT,
    COMPANY_ADMIN,
    COMPANY_AGENT,
    ADMIN,
    SUPER_USER;


    @Override
    public String getAuthority() {
        return this.name();
    }
}
