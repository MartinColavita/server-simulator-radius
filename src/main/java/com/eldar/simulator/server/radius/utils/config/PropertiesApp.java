package com.eldar.simulator.server.radius.utils.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesApp {
    @Value("${radius.server.port}")
    private String port;

    @Value("${radius.server.environment}")
    private String environment;

    @Value("${radius.server.waitTimeout}")
    private String waitTimeout;


    @Bean
    public PropertiesBeanApp propertiesBean() {
        return new PropertiesBeanApp(port,environment,waitTimeout);
    }
}
