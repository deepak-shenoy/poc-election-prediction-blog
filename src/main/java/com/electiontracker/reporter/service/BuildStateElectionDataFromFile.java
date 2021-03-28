package com.electiontracker.reporter.service;

import com.electiontracker.reporter.entities.StateData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class BuildStateElectionDataFromFile {

    @Autowired
    ObjectMapper objectMapper;

    Path dataFile = Paths.get("stateElectionData.txt");

    public List<StateData> getJsonStateElectionListFromFile() {
        List<StateData> jsonList = null;
        try {
            final List<String> lines = Files.readAllLines(dataFile);
            String fileContents = String.join("", lines);
            StateData[] stateData = objectMapper.readValue(fileContents, StateData[].class);
            jsonList = Arrays.asList(stateData);
        } catch (Exception e) {
            //
        }
        return jsonList;
    }
}
