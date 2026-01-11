package models;

import java.util.List;
import java.util.ArrayList;

public class Forecast {
    private List<String> forecastDetails;

    public Forecast() {
        this.forecastDetails = new ArrayList<>();
    }

    public void addDailyForecast(String dailyForecast) {
        this.forecastDetails.add(dailyForecast);
    }

    public List<String> getForecastDetails() {
        return forecastDetails;
    }

    public void setForecastDetails(List<String> forecastDetails) {
        this.forecastDetails = forecastDetails;
    }

    /**
     * Provides methods to fetch weekly forecasts.
     * For simplicity, this example just returns the stored details.
     * In a real application, this would involve more complex logic (e.g., API calls).
     * @return A list of weekly forecast details.
     */
    public List<String> getWeeklyForecast() {
        // In a real application, you might fetch this from an external source or generate it.
        return new ArrayList<>(forecastDetails); // Return a copy to prevent external modification
    }

    @Override
    public String toString() {
        if (forecastDetails.isEmpty()) {
            return "No forecast available.";
        }
        StringBuilder sb = new StringBuilder("Weekly Forecast:\n");
        for (int i = 0; i < forecastDetails.size(); i++) {
            sb.append("Day ").append(i + 1).append(": ").append(forecastDetails.get(i)).append("\n");
        }
        return sb.toString();
    }
}
