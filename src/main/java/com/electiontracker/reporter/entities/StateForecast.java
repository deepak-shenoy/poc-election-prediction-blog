package com.electiontracker.reporter.entities;

import lombok.Data;

@Data
public class StateForecast {
    private String stateName;
    private String y2020;

    public static StateForecast create(String stateName, String y2020) {
        StateForecast stateForecast = new StateForecast();
        stateForecast.setStateName(stateName);
        stateForecast.setY2020(y2020);
        return stateForecast;
    }
}
