package com.electiontracker.reporter.controller;

import com.electiontracker.reporter.entities.ForecastReport;
import com.electiontracker.reporter.entities.StateData;
import com.electiontracker.reporter.entities.StateForm;
import com.electiontracker.reporter.entities.SummaryForecastReport;
import com.electiontracker.reporter.service.AwsS3Service;
import com.electiontracker.reporter.service.BuildStateElectionDataFromFile;
import com.electiontracker.reporter.service.ForecastReportService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping
public class ReporterController extends AbstractBaseController{

    @Autowired BuildStateElectionDataFromFile buildStateElectionDataFromFile;
    @Autowired ForecastReportService forecastReportService;
    @Autowired AwsS3Service awsS3Service;

    @GetMapping(value={"/"})
    public String getMainReport(Model model, HttpServletRequest request) {

        auditAction("requested_report", request);

        List<StateData> stateDataList = buildStateElectionDataFromFile.getJsonStateElectionListFromFile();
        List<SummaryForecastReport> summaryForecastReportList = forecastReportService.getSummaryData();

        StateForm stateForm = new StateForm();
        stateForm.setStateDataList(stateDataList);
        stateForm.setDemocraticElectoralCollegeVotes(getDemocraticElectoralCollegeVotes(stateDataList));
        stateForm.setRepublicanElectoralCollegeVotes(getRepublicanElectoralCollegeVotes(stateDataList));

        model.addAttribute("stateForm", stateForm);
        model.addAttribute("summaryForecasts", summaryForecastReportList);
        return "mainReport";
    }

    @RequestMapping(value={"/"}, method = RequestMethod.POST)
    public String enterPrediction(@ModelAttribute StateForm stateForm, BindingResult bindingResult, Model model, HttpServletRequest request) {

        List<SummaryForecastReport> summaryForecastReportList = forecastReportService.getSummaryData();
        List<StateData> stateDataList = stateForm.getStateDataList();

        String username = auditAction("submitted_results", request);

        stateForm.setDemocraticElectoralCollegeVotes(getDemocraticElectoralCollegeVotes(stateDataList));
        stateForm.setRepublicanElectoralCollegeVotes(getRepublicanElectoralCollegeVotes(stateDataList));

        forecastReportService.saveForecast(stateForm, username);

        model.addAttribute("stateForm", stateForm);
        model.addAttribute("summaryForecasts", summaryForecastReportList);

        return "mainReport";
    }

    @GetMapping(value={"/detail/{objectKeyName}"})
    public String getItemReportDetail(@PathVariable String  objectKeyName, Model model, HttpServletRequest request) {
        ForecastReport forecastReport = forecastReportService.getForecastReport(objectKeyName);
        String username = auditAction("detailed_request", request);
        String forecastComments;
        if(StringUtils.isBlank(forecastComments=forecastReport.getComments())) {
            forecastComments = "No comments provided";
        };
        model.addAttribute("forecastComments", forecastComments);
        model.addAttribute("username", forecastReport.getUsername());
        model.addAttribute("democraticElectoralCollegeVotes",forecastReport.getDemocraticElectoralCollegeVotes());
        model.addAttribute("republicanElectoralCollegeVotes", forecastReport.getRepublicanElectoralCollegeVotes());
        model.addAttribute("dateTime", forecastReport.getDateTime());
        model.addAttribute("stateForecastList", forecastReport.getStateForecastList());
        return "detailedReport";
    }

    protected Logger log() {
        return log;
    }

}
