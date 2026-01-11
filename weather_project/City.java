package models;

public class City {
    private String cityName;
    private String state;
    private String country;

    public City(String cityName, String state, String country) {
        this.cityName = cityName;
        this.state = state;
        this.country = country;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return cityName + ", " + state + ", " + country;
    }
}