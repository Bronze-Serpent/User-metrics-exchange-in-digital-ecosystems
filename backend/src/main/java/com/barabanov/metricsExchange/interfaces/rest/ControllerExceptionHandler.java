package com.barabanov.metricsExchange.interfaces.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestHandlerErrorData> exception(Throwable e, HttpServletRequest httpRequest) {
        log.error("""
                        При обработке REST запроса произошла ошибка.
                        HTTP-body: {}
                        HTTP-parameters: {}
                        HTTP-headers: {}
                        """,
                getRequestBodyAsString(httpRequest), getParametersAsStringFrom(httpRequest), getAllHeadersFrom(httpRequest), e);

        return ResponseEntity.internalServerError()
                .body(RestHandlerErrorData.builder()
                        .message(e.getMessage())
                        .rootClassName(getRootClassName(e))
                        .stackTrace(Optional.ofNullable(e.getStackTrace())
                                .map(Arrays::toString)
                                .orElse(null))
                        .build());
    }

    private String getRootClassName(Throwable throwable) {
        if (throwable == null)
            return null;

        Throwable rootThrowable = throwable;
        while (throwable.getCause() != null)
            rootThrowable = throwable.getCause();

        return rootThrowable.getClass().getName();
    }


    private Map<String, String> getAllHeadersFrom(HttpServletRequest httpRequest) {
        try {
            Enumeration<String> headerNames = httpRequest.getHeaderNames();
            if (headerNames == null)
                return Collections.emptyMap();

            Map<String, String> headers = new HashMap<>();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, httpRequest.getHeader(headerName));
            }
            return headers;
        } catch (Throwable e) {
            log.error("При получении хэдеров HTTP запроса возникла ошибка.", e);
            return Collections.emptyMap();
        }
    }


    private String getParametersAsStringFrom(HttpServletRequest httpRequest) {
        try {
            Map<String, String[]> parameterMap = httpRequest.getParameterMap();
            if (CollectionUtils.isEmpty(parameterMap))
                return null;

            return parameterMap.entrySet().stream()
                    .map(entry -> entry.getKey() + "->" + Optional.ofNullable(entry.getValue())
                            .map(valuesArr -> Arrays.stream(valuesArr)
                                    .collect(Collectors.joining(", ", "[", "]"))))
                    .collect(Collectors.joining(";", "{", "}"));
        } catch (Throwable e) {
            log.error("При получении параметров HTTP запроса возникла ошибка.", e);
            return null;
        }
    }


    private String getRequestBodyAsString(HttpServletRequest httpRequest) {
        //TODO: wrapper как будто не работает, проверить (особенно если не был вычитан текст и когда вычитан тоже)
        try {
            ContentCachingRequestWrapper httpRequestWrapper = (ContentCachingRequestWrapper) httpRequest;
            String requestBodyAsStr = httpRequestWrapper.getContentAsString();
            if (!StringUtils.hasText(requestBodyAsStr))
                requestBodyAsStr = "Тело запроса является пустым или не использовалось при обработке запроса в приложении";

            return requestBodyAsStr;
        } catch (Throwable e) {
            log.error("При получении тела HTTP запроса возникла ошибка.", e);
            return null;
        }
    }

}
