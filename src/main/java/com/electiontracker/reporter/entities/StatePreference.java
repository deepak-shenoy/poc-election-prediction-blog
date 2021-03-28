package com.electiontracker.reporter.entities;

import lombok.Data;

@Data
public class StatePreference {

    private String id;
    private String value;
    private String label;

    StatePreference (String id, String value, String label) {
        this.id = id;
        this.value = value;
        this.label = label;
    }

    public static StatePreference of(String id, String value, String label) {
        return new StatePreference(id, value, label);
    }
}
