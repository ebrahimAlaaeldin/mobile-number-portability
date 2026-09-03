package com.mnp.mobilenumberportability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling // powers the pending-request timeout job
public class MnpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MnpApplication.class, args);
    }

}
