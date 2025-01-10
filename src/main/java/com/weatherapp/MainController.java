package com.weatherapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainController {

    @FXML private Label temperatureLabel, skyConditionLabel, cityLabel, windLabel, humidityLabel, visibilityLabel, pressureLabel, monthLabel, dayLabel, timeLabel;
    @FXML private ImageView weatherDayOrNightImage, weatherImage;
    @FXML private TextField tfName;
    @FXML public WebView forecastWebView;
    @FXML private MenuButton menuButton; // MenuButton for unit selection
    @FXML private MenuItem mnuMetric, mnuImperial; // MenuItems for units

    private Stage mainWindow;
    private ApiHelper apiHelper;  // Ensure ApiHelper is imported and used here

    public void setMainWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
        this.mainWindow.setOnCloseRequest(this::handleCloseRequest);
    }

    public MainController() {
        apiHelper = new ApiHelper();  // Instantiate the ApiHelper class
    }

    @FXML
    private void initialize() throws ParseException {
        // Set default unit to Metric
        mnuMetric.setOnAction(e -> setUnit("Metric"));
        mnuImperial.setOnAction(e -> setUnit("Imperial"));

        // Automatically load Manila's weather and forecast when the app starts
        String defaultLocation = "Manila";
        updateWeatherData(defaultLocation);
        updateForecast(defaultLocation);
    }

    private String selectedUnit = "Metric"; // Default unit

    private void setUnit(String unit) {
        selectedUnit = unit;
    }

    @FXML
    private void onBtnClick(ActionEvent event) throws ParseException {
        String location = tfName.getText().trim();
        if (!location.isEmpty()) {
            location = location.replaceAll(" ", "+");
            updateWeatherData(location);
            updateForecast(location);
        }
    }

    @FXML
    private void onBtnCloseClick(ActionEvent event) throws ParseException {
        handleCloseRequest(null);
    }

    @FXML
private void handleMetricSystem(ActionEvent event) throws ParseException {
    // Handle the selection for Metric system
    System.out.println("Metric system selected");
    updateUnit("metric");
}

@FXML
private void handleDefaultSystem(ActionEvent event) throws ParseException {
    // Handle the selection for Standard system
    System.out.println("Default system selected");
    updateUnit("default");
}

@FXML
private void handleImperialSystem(ActionEvent event) throws ParseException {
    // Handle the selection for Imperial system
    System.out.println("Imperial system selected");
    updateUnit("imperial");
}

@FXML
private void handleHistory(ActionEvent event) {
    // Handle the selection for History
    System.out.println("History selected");
}


    private void updateWeatherData(String location) {
        try {
            String unit = selectedUnit.equals("Metric") ? "metric" : "imperial";
            WeatherData weatherData = apiHelper.getWeatherData(location, unit);

            double temperature = weatherData.getTemperature();
            String skyCondition = weatherData.getSkyCondition();
            String city = weatherData.getCity();
            double windSpeed = weatherData.getWindSpeed();
            int humidity = weatherData.getHumidity();
            int visibility = weatherData.getVisibility();
            int pressure = weatherData.getPressure();
            long timestamp = weatherData.getTimestamp();
            long sunrise = weatherData.getSunrise();
            long sunset = weatherData.getSunset();

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String time = timeFormat.format(new Date(timestamp * 1000));

            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
            SimpleDateFormat dayFormat = new SimpleDateFormat("d");
            String month = monthFormat.format(new Date(timestamp * 1000));
            String day = dayFormat.format(new Date(timestamp * 1000));

            temperatureLabel.setText(String.format("%.1f", temperature) + (unit.equals("metric") ? "°C" : "°F"));
            skyConditionLabel.setText(skyCondition);
            cityLabel.setText(city);
            windLabel.setText(String.format("%.1f", windSpeed) + (unit.equals("metric") ? " m/s" : " mph"));
            humidityLabel.setText(humidity + "%");
            visibilityLabel.setText((visibility / 1000) + " km");
            pressureLabel.setText(pressure + " hPa");
            timeLabel.setText("Time: " + time);
            monthLabel.setText(month);
            dayLabel.setText(day);

            long currentTime = System.currentTimeMillis() / 1000;
            if (currentTime >= sunrise && currentTime <= sunset) {
                weatherDayOrNightImage.setImage(new Image(getClass().getResource("/com/weatherapp/images/day.png").toExternalForm()));
            } else {
                weatherDayOrNightImage.setImage(new Image(getClass().getResource("/com/weatherapp/images/night.png").toExternalForm()));
            }

            String iconCode = weatherData.getIconCode();
            String iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";
            weatherImage.setImage(new Image(iconUrl));

        } catch (IOException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void updateForecast(String location) throws ParseException {
        try {
            String unit = selectedUnit.equals("Metric") ? "metric" : "imperial";
            // Fetch forecast data with selected unit
            String forecastJson = apiHelper.getForecastData(location, unit);
            JSONObject jsonObject = new JSONObject(forecastJson);
            JSONArray hourlyForecasts = jsonObject.getJSONArray("list");
    
            forecastWebView.setStyle("-fx-background-color:rgb(149, 145, 167); -fx-padding: 0 10px 0 0; -fx-border-width: 0;");
            forecastWebView.getEngine().setUserStyleSheetLocation(getClass().getResource("custom-style.css").toExternalForm());
    
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><head><style>")
                       .append("body { background-color:rgb(168, 175, 214); margin: 0; padding: 0; overflow-x: auto; overflow-y: hidden; }")
                       .append(".forecast-container { display: flex; flex-direction: row; overflow-x: scroll; }")
                       .append(".hourly-item { display: inline-block; width: 100px; text-align: center; margin: 10px; }")
                       .append(".hourly-item img { width: 50px; height: 50px; margin-bottom: 5px; }")
                       .append("</style></head><body>");
    
            htmlBuilder.append("<div class='forecast-container'>");
    
            for (int i = 0; i < 8; i++) {
                JSONObject hourData = hourlyForecasts.getJSONObject(i);
                long timestamp = hourData.getLong("dt");
                String dateTime = new SimpleDateFormat("HH:mm").format(new Date(timestamp * 1000));
                double temp = hourData.getJSONObject("main").getDouble("temp");
                String condition = hourData.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = hourData.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + ".png";
    
                htmlBuilder.append("<div class='hourly-item'>")
                           .append("<span>").append(dateTime).append("</span><br>")
                           .append("<img src='").append(iconUrl).append("' alt='").append(condition).append("'/><br>")
                           .append("<span>").append(String.format("%.1f", temp) + (unit.equals("metric") ? "°C" : "°F")).append("</span>")
                           .append("</div>");
            }
    
            htmlBuilder.append("</div></body></html>");
            forecastWebView.getEngine().loadContent(htmlBuilder.toString());
    
        } catch (IOException e) {
            System.out.println("Error fetching forecast data: " + e.getMessage());
        }
    }

    private void handleCloseRequest(WindowEvent event) {
        System.out.println("Closing application...");
        System.exit(0);
    }

    private void updateUnit(String unit) throws ParseException {
        // Update the unit for your API call or UI
        System.out.println("Unit updated to: " + unit);
    
        // Perform necessary changes, such as reloading the weather data
        String location = tfName.getText().trim();
        if (!location.isEmpty()) {
            location = location.replaceAll(" ", "+");
            
            // Update the weather and forecast using the selected unit
            updateWeatherData(location);  // Reload the current weather
            updateForecast(location);     // Reload the forecast with the updated unit
            
            // Update any UI elements, like unit labels, if necessary
            updateUIWithUnit(unit);
        }
    }
    
    // Update the UI to reflect the unit (metric or imperial)
    private void updateUIWithUnit(String unit) {
        // Here you can update any UI elements that display units like temperature, wind speed, etc.
        // For example, you could change the unit on labels for temperature or wind speed if necessary:
        
        String unitLabel = unit.equals("metric") ? "°C" : "°F"; // Modify temperature unit
        temperatureLabel.setText(temperatureLabel.getText().replaceAll("°C|°F", unitLabel)); // Update temperature label
        
        // You can similarly update other labels related to the unit (wind, pressure, etc.)
        windLabel.setText(windLabel.getText().replaceAll("m/s|mph", unit.equals("metric") ? " m/s" : " mph"));
        humidityLabel.setText(humidityLabel.getText().replace("%", "%"));
        visibilityLabel.setText(visibilityLabel.getText().replaceAll("km", unit.equals("metric") ? " km" : " miles"));
        pressureLabel.setText(pressureLabel.getText().replace("hPa", "hPa"));
        
        // You can also reload any other UI elements that depend on the unit change.
    }
    
    
}
