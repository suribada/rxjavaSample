package com.suribada.rxjavabook.api.model;

/**
 {
 "weatherCode" : 10,
 "weatherText" : "흐리고 갬",
 "currentTemperature" : 10.0
 }
 * Created by Noh.Jaechun on 2018-04-08.
 */
public class Weather {
    public int weatherCode;
    public String weatherText;
    public float currentTemperature;

    @Override
    public String toString() {
        return "Weather{" +
                "weatherCode=" + weatherCode +
                ", weatherText='" + weatherText + '\'' +
                ", currentTemperature=" + currentTemperature +
                '}';
    }

    public static Weather create(int weatherCode, String weatherText, float currentTemperature) {
        Weather weather = new Weather();
        weather.weatherCode = weatherCode;
        weather.weatherText = weatherText;
        weather.currentTemperature = currentTemperature;
        return weather;
    }

}
