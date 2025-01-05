# Weather Tracker Spring Boot application

If you want to test the app., make sure to set the environment variable "API_KEY" with the OpenWeatherMap API key.

### **Step 1: Set Up the Spring Boot Project**
1. **Create a Project Using Spring Initializr**:
   - Go to [Spring Initializr](https://start.spring.io/).
   - Select:
     - **Project**: Maven
     - **Language**: Java
     - **Spring Boot Version**: 3.x.x (latest stable).
   - Add dependencies:
     - Spring Web (for REST APIs).
     - Spring Boot DevTools (for hot reload).
     - OpenFeign (for external API calls).
   - Generate the project and extract the `.zip` file.

2. **Import into IDE**:
   - Open your IDE (e.g., IntelliJ IDEA, Eclipse).
   - Import the project as a Maven project.

---

### **Step 2: Get an API Key from OpenWeatherMap**
- Sign up at [OpenWeatherMap](https://openweathermap.org/).
- Generate a free API key for fetching weather data.

---

### **Step 3: Define the Data Model**
Create a class to represent the weather data returned by the API.

```java
package com.example.weathertracker.model;

public class WeatherResponse {
    private String city;
    private String description;
    private double temperature;

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
```

---

### **Step 4: Configure OpenFeign Client**
Create a Feign client to fetch data from the OpenWeatherMap API.

1. Add Feign dependency to `pom.xml` if not already included:
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-openfeign</artifactId>
   </dependency>
   ```

2. Enable Feign in the main application class:
   ```java
   package com.example.weathertracker;

   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.openfeign.EnableFeignClients;

   @SpringBootApplication
   @EnableFeignClients
   public class WeatherTrackerApplication {
       public static void main(String[] args) {
           SpringApplication.run(WeatherTrackerApplication.class, args);
       }
   }
   ```

3. Create the Feign client interface:

   ```java
   package com.example.weathertracker.client;

   import org.springframework.cloud.openfeign.FeignClient;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestParam;

   @FeignClient(name = "weatherClient", url = "https://api.openweathermap.org/data/2.5")
   public interface WeatherClient {

       @GetMapping("/weather")
       Object getWeather(@RequestParam("q") String city, 
                         @RequestParam("appid") String apiKey, 
                         @RequestParam("units") String units);
   }
   ```

---

### **Step 5: Create the Service Layer**
Add a service to handle the business logic.

```java
package com.example.weathertracker.service;

import com.example.weathertracker.client.WeatherClient;
import com.example.weathertracker.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WeatherService {

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    @Autowired
    private WeatherClient weatherClient;

    public WeatherResponse getWeather(String city) {
        Map<String, Object> response = (Map<String, Object>) weatherClient.getWeather(city, API_KEY, "metric");

        // Extract necessary data
        String description = ((Map<String, Object>) ((Map<String, Object>) ((java.util.List<?>) response.get("weather")).get(0))).get("description").toString();
        double temperature = Double.parseDouble(((Map<String, Object>) response.get("main")).get("temp").toString());

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setCity(city);
        weatherResponse.setDescription(description);
        weatherResponse.setTemperature(temperature);

        return weatherResponse;
    }
}
```

---

### **Step 6: Create the Controller**
Expose REST endpoints to fetch weather data.

```java
package com.example.weathertracker.controller;

import com.example.weathertracker.model.WeatherResponse;
import com.example.weathertracker.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public WeatherResponse getWeather(@RequestParam String city) {
        return weatherService.getWeather(city);
    }
}
```

---

### **Step 7: Test the Application**
1. **Run the Application**:
   - Run the `WeatherTrackerApplication` class.
   - The app will start on `http://localhost:8080`.

2. **Test with cURL or Postman**:
   - Fetch weather data for a city:
     ```cmd
     curl -X GET "http://localhost:8080/api/weather?city=London"
     ```

   - Example Response:
     ```json
     {
       "city": "London",
       "description": "clear sky",
       "temperature": 15.32
     }
     ```

---

### **Step 8: Optional Enhancements**
1. **Add Exception Handling**:
   - Handle cases where the city is not found or the API key is invalid.

2. **Cache Weather Data**:
   - Use Spring Cache to avoid repetitive API calls for the same city.

3. **Deploy to the Cloud**:
   - Deploy the app to platforms like Heroku, AWS, or GCP.

4. **Secure the App**:
   - Use Spring Security to restrict access to authorized users.

---

The two lines of code extract specific pieces of information (`description` and `temperature`) from a nested JSON-like structure represented as a `Map<String, Object>`. This type of structure is common when parsing JSON responses from APIs in Java, where the response has been deserialized into a `Map`. Let's break each line down:

### First Line: Extracting `description`
```java
String description = ((Map<String, Object>) ((Map<String, Object>) ((java.util.List<?>) response.get("weather")).get(0))).get("description").toString();
```

#### Steps:
1. **Access the `weather` key**:
   - `response.get("weather")` retrieves the value associated with the `weather` key in the `response` map. This value is expected to be a `List`.

2. **Cast it to a `List`**:
   - `(java.util.List<?>) response.get("weather")` casts the value to a `List<?>` (wildcard type to handle any type of list).

3. **Access the first element**:
   - `.get(0)` retrieves the first element in the `weather` list. This element is expected to be a `Map<String, Object>`.

4. **Cast it to a `Map<String, Object>`**:
   - `(Map<String, Object>) ((java.util.List<?>) response.get("weather")).get(0)` casts the first element to a `Map<String, Object>`.

5. **Retrieve the `description` key**:
   - `.get("description")` retrieves the value associated with the `description` key from the map.

6. **Convert to `String`**:
   - `.toString()` converts the `Object` value to a `String`.

#### Example JSON for `weather`:
```json
"weather": [
    {
        "id": 800,
        "main": "Clear",
        "description": "clear sky",
        "icon": "01d"
    }
]
```

- The `description` value extracted would be `"clear sky"`.

---

### Second Line: Extracting `temperature`
```java
double temperature = Double.parseDouble(((Map<String, Object>) response.get("main")).get("temp").toString());
```

#### Steps:
1. **Access the `main` key**:
   - `response.get("main")` retrieves the value associated with the `main` key in the `response` map. This value is expected to be a `Map<String, Object>`.

2. **Cast it to a `Map<String, Object>`**:
   - `(Map<String, Object>) response.get("main")` casts the value to a `Map<String, Object>`.

3. **Retrieve the `temp` key**:
   - `.get("temp")` retrieves the value associated with the `temp` key from the `main` map. This value is expected to be a `String` or `Number`.

4. **Convert to `String`**:
   - `.toString()` ensures the value is converted to a `String` for parsing.

5. **Convert to `double`**:
   - `Double.parseDouble(...)` parses the `String` representation of the temperature into a `double`.

#### Example JSON for `main`:
```json
"main": {
    "temp": 293.15,
    "pressure": 1013,
    "humidity": 53
}
```

- The `temp` value extracted would be `293.15`, and the result would be the double value `293.15`.

---

### Summary
- The **first line** extracts the `description` string (e.g., `"clear sky"`) from a nested `weather` array in the response.
- The **second line** extracts the `temp` value (e.g., `293.15`) as a `double` from the `main` object in the response.

### Suggested Improvements
1. **Use a JSON library**:
   - Manually traversing nested structures can be error-prone and verbose. Libraries like Jackson or Gson simplify this process by mapping JSON to Java objects.
   
   For example, with Jackson:
   ```java
   ObjectMapper objectMapper = new ObjectMapper();
   Response response = objectMapper.readValue(jsonString, Response.class);
   String description = response.getWeather().get(0).getDescription();
   double temperature = response.getMain().getTemp();
   ```

2. **Error handling**:
   - Add null checks or use `Optional` to avoid `NullPointerException` if keys are missing or have unexpected values.