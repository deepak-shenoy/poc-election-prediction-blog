package com.electiontracker.reporter.controller;

import com.electiontracker.reporter.entities.StateData;
import com.electiontracker.reporter.service.ForecastReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
public class AbstractBaseController {

    @Autowired private ForecastReportService forecastReportService;

    public String auditAction(String action, HttpServletRequest request) {
        String username = "";
        String ipAddress = "";
        String userAgent = "";
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails)principal).getUsername();
            } else {
                username = principal.toString();
            }
            System.out.println("User :" + username + " " + action + " .");
        } catch (Exception e) {
            log().error("Couldn't get the username details.");
        }

        try {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
            if(StringUtils.isEmpty(ipAddress)) ipAddress = request.getRemoteAddr();
            userAgent = request.getHeader("User-Agent");
            log().info("User (username: " + username + ") " + action + "  from location: " + ipAddress + " with details" + userAgent);
        } catch (Exception e) {
            log().error("Could not capture user's (username: " + username + ") request details from http header.");
        }
        forecastReportService.saveAuditEntry(username, action, ipAddress, userAgent);
        return username;
    }

    public String getDemocraticElectoralCollegeVotes(List<StateData> stateDataList) {
        int democraticElectoralCollegeVotes = stateDataList.stream()
                .filter(state->state.getY2020().equalsIgnoreCase("Democrat"))
                .mapToInt(state->Integer.valueOf(state.getElectoralVotes()))
                .sum();
        return Integer.toString(democraticElectoralCollegeVotes);
    }

    public String getRepublicanElectoralCollegeVotes(List<StateData> stateDataList){
        int republicanElectoralCollegeVotes = stateDataList.stream()
                .filter(state->state.getY2020().equalsIgnoreCase("Republican"))
                .mapToInt(state->Integer.valueOf(state.getElectoralVotes()))
                .sum();
        return Integer.toString(republicanElectoralCollegeVotes);
    }

    protected Logger log() {
        return log;
    }
}
