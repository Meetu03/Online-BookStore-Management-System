package com.bookstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // reads app.upload.dir from application.properties (default uploads/images)
    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String resourceLocation = uploadPath.toUri().toString(); // file:///...

        // VERY IMPORTANT debug prints
        System.out.println("=== WebMvcConfig START ===");
        System.out.println("APP WORKING DIR: " + Paths.get("").toAbsolutePath().toString());
        System.out.println("app.upload.dir (property): " + uploadDir);
        System.out.println("Mapping /images/** -> " + resourceLocation);
        System.out.println("=== WebMvcConfig END ===");

        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600);
    }
}
