package ca.srikar.weathertracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class WeatherTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherTrackerApplication.class, args);
    }

}
