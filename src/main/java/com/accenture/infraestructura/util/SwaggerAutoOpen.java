package com.accenture.infraestructura.util;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class SwaggerAutoOpen {
    @EventListener(ApplicationReadyEvent.class)
    public void openSwaggerUI() {
        try {
            Thread.sleep(1500);
            String url = "http://localhost:7070/swagger-ui.html";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
        }
    }
}
