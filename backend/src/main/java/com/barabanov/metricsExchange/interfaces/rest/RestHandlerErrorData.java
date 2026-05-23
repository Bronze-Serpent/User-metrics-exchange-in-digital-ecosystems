package com.barabanov.metricsExchange.interfaces.rest;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestHandlerErrorData {
    String rootClassName;
    String message;
    String stackTrace;
}
