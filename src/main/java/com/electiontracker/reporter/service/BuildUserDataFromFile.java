package com.electiontracker.reporter.service;

import com.electiontracker.reporter.entities.UserAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class BuildUserDataFromFile {
    @Autowired ObjectMapper objectMapper;

    // Very bad security - for testing purposes
    Path dataFile = Paths.get("users.txt");

    public List<UserAttributes> getJsonUserListFromFile() {
        List<UserAttributes> jsonList = null;
        try {
            final List<String> lines = Files.readAllLines(dataFile);
            String fileContents = String.join("", lines);
            UserAttributes[] userAttributes = objectMapper.readValue(fileContents, UserAttributes[].class);
            jsonList = Arrays.asList(userAttributes);
        } catch (Exception e) {
            //
        }
        return jsonList;
    }
}
