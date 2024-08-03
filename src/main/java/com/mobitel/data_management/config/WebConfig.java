package com.mobitel.data_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/CSV-Files/amc/**")
                .addResourceLocations("file:src/main/resources/static/CSV-Files/amc/")
                .setCachePeriod(0);
    }
}
