package main;

import services.WeatherService;
import models.Weather;
import models.City;
import models.Forecast;
import exceptions.InvalidCityNameException;
import exceptions.MissingWeatherDataException;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class WeatherApp {
    private WeatherService weatherService;
    private Scanner scanner;

    public WeatherApp() {
        weatherService = new WeatherService();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        WeatherApp app = new WeatherApp();
        app.run();
    }

    public void run() {
        System.out.println("Welcome to the Weather Forecast Application!");

        int choice;
        do {
            displayMenu();
            try {
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        getWeatherForecast();
                        break;
                    case 2:
                        weatherService.displayAvailableCities();
                        break;
                    case 3:
                        addNewCityWeather();
                        break;
                    case 4:
                        System.out.println("Thank you for using the Weather Forecast Application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                choice = 0; // Set choice to 0 to re-display menu
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                choice = 0;
            }
            System.out.println(); // Add a newline for better readability
        } while (choice != 4);

        scanner.close();
    }

    private void displayMenu() {
        System.out.println("--- Menu ---");
        System.out.println("1. Get Weather Forecast for a City");
        System.out.println("2. Display Available Cities");
        System.out.println("3. Add New City Weather Data (Admin)"); // Optional: for demonstration
        System.out.println("4. Exit");
    }

    private void getWeatherForecast() {
        System.out.print("Enter city name: ");
        String cityName = scanner.nextLine();

        try {
            Weather weather = weatherService.getWeather(cityName);
            System.out.println("\n--- Weather in " + cityName + " ---");
            System.out.println(weather);
            System.out.println("---------------------------------");
        } catch (InvalidCityNameException | MissingWeatherDataException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void addNewCityWeather() {
        System.out.println("\n--- Add New City Weather Data ---");
        System.out.print("Enter city name: ");
        String cityName = scanner.nextLine();
        System.out.print("Enter state: ");
        String state = scanner.nextLine();
        System.out.print("Enter country: ");
        String country = scanner.nextLine();

        City newCity = new City(cityName, state, country);

        System.out.print("Enter temperature (°C): ");
        double temperature = scanner.nextDouble();
        System.out.print("Enter humidity (%): ");
        double humidity = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter weather condition (e.g., Sunny, Cloudy, Rainy): ");
        String condition = scanner.nextLine();

        System.out.println("Enter weekly forecast details (enter 'done' when finished):");
        Forecast newForecast = new Forecast();
        String dailyForecast;
        int day = 1;
        while (true) {
            System.out.print("Day " + day + " forecast: ");
            dailyForecast = scanner.nextLine();
            if (dailyForecast.equalsIgnoreCase("done")) {
                break;
            }
            newForecast.addDailyForecast(dailyForecast);
            day++;
        }

        Weather newWeather = new Weather(temperature, humidity, condition, newForecast);
        weatherService.addCityWeather(newCity, newWeather);
        System.out.println("New city weather data added successfully!");
    }
}

