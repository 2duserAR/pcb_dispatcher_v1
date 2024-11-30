package com.yadro.pcbdispatcher.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI pcbDispatcherOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PCB Dispatcher API")
                        .description("API для управления перемещением печатных плат по этапам производства")
                        .version("1.0"));
    }
} 