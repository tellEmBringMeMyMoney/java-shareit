package ru.practicum.shareit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Properties;

@Configuration
public class ValidationConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        Properties properties = new Properties();
        properties.setProperty(
                "hibernate.validator.temporal_validation_tolerance",
                "2000"
        );

        validator.setValidationProperties(properties);

        return validator;
    }
}
