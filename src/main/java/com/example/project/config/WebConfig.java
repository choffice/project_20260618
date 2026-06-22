package com.example.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/profile/**")
                .addResourceLocations("file:///C:/uploads/profile/");

        registry.addResourceHandler("/plannersThumbnail/**")
                .addResourceLocations("file:///C:/uploads/planners/thumbnail/");

        registry.addResourceHandler("/plannersBanner/**")
                .addResourceLocations("file:///C:/uploads/planners/banner/");

        registry.addResourceHandler("/scheduleThumbnail/**")
                .addResourceLocations("file:///C:/uploads/schedule/thumbnail/");
    }
}