package com.electiontracker.reporter.entities;

import lombok.Data;
import java.util.List;

@Data
public class ForecastReport {
    private List<StateForecast> stateForecastList;
    private String comments;
    private String dateTime;
    private String username;
    private String democraticElectoralCollegeVotes;
    private String republicanElectoralCollegeVotes;
    private String objectKeyName;
}
