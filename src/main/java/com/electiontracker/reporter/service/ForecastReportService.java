package com.electiontracker.reporter.service;

import com.electiontracker.reporter.entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ForecastReportService {

    @Autowired private AwsS3Service awsS3Service;
    @Autowired private ObjectMapper objectMapper;


    public boolean saveForecast(StateForm stateForm, String username) {
        String jsonForecast = "";
        ForecastReport forecastReport = new ForecastReport();
        forecastReport.setDemocraticElectoralCollegeVotes(stateForm.getDemocraticElectoralCollegeVotes());
        forecastReport.setRepublicanElectoralCollegeVotes(stateForm.getRepublicanElectoralCollegeVotes());
        forecastReport.setComments(stateForm.getComments());
        forecastReport.setUsername(username);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String dateValue = dateFormat.format(new Date(System.currentTimeMillis()));
        String timeValue = timeFormat.format(new Date(System.currentTimeMillis()));

        forecastReport.setDateTime(dateValue + " " + timeValue);
        List<StateForecast> stateForecastList = new ArrayList<>();
        stateForecastList = stateForm.getStateDataList().stream()
                .map(stateItem->(StateForecast.create(stateItem.getStateName(), stateItem.getY2020())))
                .collect(Collectors.toList());
        forecastReport.setStateForecastList(stateForecastList);

        try {
            jsonForecast = objectMapper.writeValueAsString(forecastReport);
        } catch (JsonProcessingException e) {
            log().error("Could not convert forecast to json ", e);
            e.printStackTrace();
            return false;
        }
        String filename = dateValue + "_" + timeValue + "_" + username;
        return awsS3Service.saveDataToS3Object(AwsS3Service.ELECTION_DATA_BUCKET_NAME, filename, jsonForecast);
    }

    public boolean saveAuditEntry(String username, String action, String ipAddress, String userAgent) {
        String jsonAuditEntry = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String dateValue = dateFormat.format(new Date(System.currentTimeMillis()));
        String timeValue = timeFormat.format(new Date(System.currentTimeMillis()));

        AuditEntry auditEntry = new AuditEntry(dateValue + " " + timeValue, username, action, ipAddress
        , userAgent);

        String filename = dateValue + "_" + timeValue + "_" + username;

        try {
            jsonAuditEntry = objectMapper.writeValueAsString(auditEntry);
        } catch (JsonProcessingException e) {
            log().error("Could not convert forecast to json ", e);
            e.printStackTrace();
            return false;
        }
        return awsS3Service.saveDataToS3Object(AwsS3Service.AUDIT_BUCKET_NAME, filename, jsonAuditEntry);
    }

    public List<SummaryForecastReport> getSummaryData() {
        List<ForecastReport> forecastReportList = awsS3Service.getSavedListOfForecastReports();
        if (forecastReportList == null) {
            return null;
        }
        List<SummaryForecastReport> summaryForecastReportList = forecastReportList.stream()
                .map(report->SummaryForecastReport.create(report.getObjectKeyName(),report.getDateTime(), report.getUsername(), report.getDemocraticElectoralCollegeVotes(), report.getRepublicanElectoralCollegeVotes(), report.getComments()))
                .collect(Collectors.toList());
        Collections.sort(summaryForecastReportList);
        return summaryForecastReportList;
    }

    public ForecastReport getForecastReport(String objectKeyName) {
        ForecastReport forecastReport = new ForecastReport();
        forecastReport = awsS3Service.getSavedForecastReport(objectKeyName);
        return  forecastReport;
    }



    protected Logger log() {
        return log;
    }

}
