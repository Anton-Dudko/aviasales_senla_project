package eu.senla.tripservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TripserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripserviceApplication.class, args);
    }

}
