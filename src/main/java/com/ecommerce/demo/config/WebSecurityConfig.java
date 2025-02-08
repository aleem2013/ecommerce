package com.ecommerce.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {
   @Override
   public void addCorsMappings(@NonNull CorsRegistry registry) {
       registry.addMapping("/**")
           .allowedOriginPatterns("http://localhost:[*]")
           .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
           .allowedHeaders("*")
           .exposedHeaders("Authorization")
           .allowCredentials(true)
           .maxAge(3600);
   }
}
