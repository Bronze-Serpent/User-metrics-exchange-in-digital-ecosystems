package com.barabanov.metricsExchange.interfaces.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;


@Component
public class CachingRequestBodyFilter extends OncePerRequestFilter {

    // Этот wrapper нужен для кэширования body из HttpServletRequest, в случае его вычитки.
    // Чтобы можно было получить доступ к body внутри HttpServletRequest при обработке ошибки
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request, 1000 * 100);

        filterChain.doFilter(wrapperRequest, response);
    }
}
