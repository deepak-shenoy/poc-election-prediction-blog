package com.electiontracker.reporter.entities;

import lombok.Data;

@Data
public class StateData {
    private String stateName;
    private String weighting;
    private String electoralVotes;
    private String y2000;
    private String y2004;
    private String y2008;
    private String y2012;
    private String y2016;
    private String y2020;
    private String notes;
}
