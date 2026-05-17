package com.barabanov.metricsExchange.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class WebServerConfigLogger {

    @EventListener
    public void onServerStarted(WebServerInitializedEvent event) {
        if (event.getWebServer() instanceof TomcatWebServer tomcatWebServer) {
            if (tomcatWebServer.getTomcat().getConnector().getProtocolHandler()
                    instanceof AbstractHttp11Protocol<?> coyoteAbstractProtocol) {
                log.info("Tomcat web server работает с максимальноым количеством потоков равным: {}", coyoteAbstractProtocol.getMaxThreads());
            }
        }
    }
}
