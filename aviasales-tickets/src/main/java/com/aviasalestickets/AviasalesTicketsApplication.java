package com.aviasalestickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AviasalesTicketsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AviasalesTicketsApplication.class, args);
    }

}
