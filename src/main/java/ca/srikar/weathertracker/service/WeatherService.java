package ca.srikar.weathertracker.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import ca.srikar.weathertracker.client.WeatherClient;
import ca.srikar.weathertracker.model.WeatherResponse;

@Service
public class WeatherService {

    private static final String API_KEY = System.getenv("API_KEY");

    private WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public WeatherResponse getWeather(String city) {
        Map<String, Object> response = (Map<String, Object>) weatherClient.getWeather(city, API_KEY, "metric");

        String description = ((Map<String, Object>) ((Map<String, Object>) ((java.util.List<?>) response.get("weather")).get(0))).get("description").toString();
        double temperature = Double.parseDouble(((Map<String, Object>) response.get("main")).get("temp").toString());

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setDescription(description);
        weatherResponse.setTemperature(temperature);
        weatherResponse.setCity(city);

        return weatherResponse;
    }
}
