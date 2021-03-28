package com.electiontracker.reporter.entities;

import lombok.Data;

@Data
public class AuditEntry {
    String dateTime;
    String username;
    String action;
    String ipAddress;
    String userAgent;

    public AuditEntry(String dateTime, String username, String action, String ipAddress, String userAgent) {
        this.dateTime = dateTime;
        this.username = username;
        this.action = action;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}
