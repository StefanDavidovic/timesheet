package com.example.billing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;

@Configuration
@PropertySource({"classpath:configuration.properties"})
public class BillingConfig implements Serializable {

    @Value("${billing.pathFile}")
    String pathFile;

    @Value("${billing.token}")
    String token;

    @Value("${billing.timeSheetUrl}")
    String timeSheetUrl;

    @Bean
    public String pathFile(){
        return pathFile;
    } 

    @Bean
    public String token(){
        return token;
    }

    @Bean
    public String timeSheetUrl(){
        return timeSheetUrl;
    }

}
