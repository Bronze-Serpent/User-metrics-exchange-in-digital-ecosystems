package com.barabanov.metricsExchange.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
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
                log.info("Tomcat web server работает с максимальным количеством потоков равным: {}", coyoteAbstractProtocol.getMaxThreads());
            }
        }
    }
}
