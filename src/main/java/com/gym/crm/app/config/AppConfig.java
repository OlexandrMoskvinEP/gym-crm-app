package com.gym.crm.app.config;

import com.gym.crm.app.YamlPropertySourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = "com.gym.crm.app")
@PropertySource(value = "classpath:application.yml",factory = YamlPropertySourceFactory.class)
public class AppConfig {
}
