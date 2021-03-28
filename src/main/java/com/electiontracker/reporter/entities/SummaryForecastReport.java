package com.electiontracker.reporter.entities;

import lombok.Data;

@Data
public class SummaryForecastReport implements Comparable<SummaryForecastReport> {
    private String comments;
    private String dateTime;
    private String username;
    private String democraticElectoralCollegeVotes;
    private String republicanElectoralCollegeVotes;
    private String objectKeyName;

    public static SummaryForecastReport create(String objectKeyName, String dateTime, String username, String democraticElectoralCollegeVotes, String republicanElectoralCollegeVotes, String comments) {
        SummaryForecastReport summaryForecastReport = new SummaryForecastReport();
        summaryForecastReport.setObjectKeyName(objectKeyName);
        summaryForecastReport.setDateTime(dateTime);
        summaryForecastReport.setUsername(username);
        summaryForecastReport.setDemocraticElectoralCollegeVotes(democraticElectoralCollegeVotes);
        summaryForecastReport.setRepublicanElectoralCollegeVotes(republicanElectoralCollegeVotes);
        summaryForecastReport.setComments(comments);
        return summaryForecastReport;
    }

    @Override
    public int compareTo(SummaryForecastReport summaryForecastReport) {
        return summaryForecastReport.getDateTime().compareTo(this.getDateTime());
    }
}
