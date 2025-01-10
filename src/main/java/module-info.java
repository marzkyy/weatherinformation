module com.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.json;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;  // Use the core5 dependency explicitly

    opens com.weatherapp to javafx.fxml;
    exports com.weatherapp;
}
