package models;

public class Weather {
    private double temperature; // in Celsius or Fahrenheit, depending on preference
    private double humidity;    // in percentage
    private String condition;   // e.g., "Sunny", "Cloudy", "Rainy"
    private Forecast forecast;

    public Weather(double temperature, double humidity, String condition, Forecast forecast) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
        this.forecast = forecast;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    @Override
    public String toString() {
        return "Temperature: " + temperature + "°C, Humidity: " + humidity + "%, Condition: " + condition + "\n" + forecast.toString();
    }
}