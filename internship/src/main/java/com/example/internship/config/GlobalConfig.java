package com.example.internship.config;

import com.example.internship.InternshipApplication;
import com.example.internship.repository.TeamMemberRepo;
import com.example.internship.service.impl.SendMailService;
import com.example.internship.service.impl.TeamMemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

@Configuration
@PropertySource({"classpath:configuration.properties"})
public class GlobalConfig implements Serializable {

    @Value("${internship.logging.level}")
    private String logLevel;
    @Value("${internship.employeeUrl}")
    private String employeeUrl;

    @Value("${internship.hostMail}")
    private String hostMail;

    @Value("${internship.portMail}")
    private String portMail;

    @Value("${internship.logging.pathFile}")
    private String filePath;

    @Bean
    @Scope("singleton")
    @Primary
    public Logger log(){
        var logger = Logger.getLogger(InternshipApplication.class.getName());

        if(logLevel != null){
            logger.setLevel(Level.parse(logLevel));
        }
        FileHandler fh;
        try {
            fh = new FileHandler(filePath+"logFIle.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

    @Bean
    public String employeeUrl(){
        return employeeUrl;
    }

    @Bean
    @Primary
    public String hostMail(){
        return hostMail;
    }

    @Bean
    public String getPortMail(){
        return portMail;
    }

}
