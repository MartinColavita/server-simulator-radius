package com.eldar.simulator.server.radius.utils.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PropertiesBeanApp {
    private String port;
    private String environment;
    private String waitTimeout;
}
