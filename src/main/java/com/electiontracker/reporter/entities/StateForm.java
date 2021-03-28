package com.electiontracker.reporter.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StateForm {
    private List<StateData> stateDataList;
    private String democraticElectoralCollegeVotes;
    private String republicanElectoralCollegeVotes;
    private String comments = "";
}
