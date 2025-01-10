package com.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONObject;

public class WeatherData {

    private double temperature;
    private String skyCondition;
    private String city;
    private double windSpeed;
    private int humidity;
    private int visibility;
    private int pressure;
    private long sunrise;
    private long sunset;
    private String iconCode;
    private long timestamp;
    private String unit;  // Store the unit of temperature (e.g., "metric" or "imperial")

    // Constructor that takes a JSONObject and initializes fields
    public WeatherData(JSONObject jsonObject, String unit) {
        this.temperature = jsonObject.getJSONObject("main").getDouble("temp");
        this.skyCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        this.city = jsonObject.getString("name");
        this.windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        this.humidity = jsonObject.getJSONObject("main").getInt("humidity");
        this.visibility = jsonObject.getInt("visibility");
        this.pressure = jsonObject.getJSONObject("main").getInt("pressure");
        this.sunrise = jsonObject.getJSONObject("sys").getLong("sunrise");
        this.sunset = jsonObject.getJSONObject("sys").getLong("sunset");
        this.iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
        this.timestamp = jsonObject.getLong("dt");
        this.unit = unit;  // Initialize the unit of measurement
    }

    // Getter methods
    public double getTemperature() {
        return temperature;
    }

    public String getSkyCondition() {
        return skyCondition;
    }

    public String getCity() {
        return city;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getVisibility() {
        return visibility;
    }

    public int getPressure() {
        return pressure;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public String getIconCode() {
        return iconCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUnit() {
        return unit;
    }

    // Method to get formatted timestamp (Day, Month, Time)
    public String getFormattedDate() {
        Date date = new Date(timestamp * 1000L); // Convert from seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    // Method to get formatted time (Hour:Minute:Second)
    public String getFormattedTime() {
        Date date = new Date(timestamp * 1000L); // Convert from seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    // Method to get the Day of the week
    public String getDayOfWeek() {
        Date date = new Date(timestamp * 1000L); // Convert from seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    // Method to get the Month
    public String getMonth() {
        Date date = new Date(timestamp * 1000L); // Convert from seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    // Optional: Method to get the icon URL for the weather
    public String getIconUrl() {
        return "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";  // URL for icon image
    }

    // Optional: Method to format the temperature based on the unit
    public String getFormattedTemperature() {
        if (unit.equals("metric")) {
            return String.format("%.2f°C", temperature);  // Format for Celsius
        } else if (unit.equals("imperial")) {
            return String.format("%.2f°F", temperature);  // Format for Fahrenheit
        } else {
            return String.format("%.2f K", temperature);  // Format for Kelvin
        }
    }
}
