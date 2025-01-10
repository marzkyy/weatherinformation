package com.weatherapp;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class ApiHelper {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String API_KEY = "08ff1488ae3b0074f493f57da3481e27";  // Replace with your actual API key

    private String unitSystem = "metric"; // Default unit system is metric

    public void setUnitSystem(String unitSystem) {
        this.unitSystem = unitSystem;
    }

    // Method to get current weather data
    public WeatherData getWeatherData(String location, String unit) throws IOException, ParseException {
        // If the unit is not provided, use the default unitSystem
        if (unit == null || unit.isEmpty()) {
            unit = unitSystem;
        }

        // Build the URL with the location, unit, and API key
        String url = BASE_URL + "?q=" + location + "&units=" + unit + "&appid=" + API_KEY;

        // Fetch the weather data and parse it into a WeatherData object
        return fetchWeatherData(url, unit);
    }

    // Method to get forecast data
    public String getForecastData(String location, String unit) throws IOException, ParseException {
        // If the unit is not provided, use the default unitSystem
        if (unit == null || unit.isEmpty()) {
            unit = unitSystem;
        }

        // Build the URL for the forecast API
        String url = FORECAST_URL + "?q=" + location + "&units=" + unit + "&appid=" + API_KEY;

        // Fetch the forecast data and return the raw JSON response as a string
        return fetchData(url);
    }

    // Private method to fetch weather data and return a WeatherData object
    private WeatherData fetchWeatherData(String url, String unit) throws IOException, ParseException {
        // Get the response from the API
        String response = fetchData(url);

        // Parse the response into a JSON object
        JSONObject jsonResponse = new JSONObject(response);

        // Return a WeatherData object, passing the JSON response and the unit
        return new WeatherData(jsonResponse, unit);
    }

    // Private method to send the HTTP request and fetch data from the API
    private String fetchData(String url) throws IOException, ParseException {
        // Make the HTTP request and fetch data from the API
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();

                // Check the response status code
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        // Return the response content as a string
                        return EntityUtils.toString(entity);
                    } else {
                        throw new IOException("Empty response from server.");
                    }
                } else {
                    throw new IOException("Failed to fetch data: HTTP Status Code " + statusCode);
                }
            }
        }
    }
}
