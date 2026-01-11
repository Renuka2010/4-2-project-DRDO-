package exceptions;

public class MissingWeatherDataException extends Exception {
    public MissingWeatherDataException(String message) {
        super(message);
    }
}