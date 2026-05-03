package com.teachsync.teachsyncevents.system;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class SystemAlertEvent extends BaseEvent {

    private String sourceServiceName;
    private String operation;
    private String dependency;
    private String severity;
    private String message;
    private String technicalDetails;

    public SystemAlertEvent() {
    }

    public SystemAlertEvent(String sourceServiceName,
                            String operation,
                            String dependency,
                            String severity,
                            String message,
                            String technicalDetails) {
        super(sourceServiceName, ActionTypes.SYSTEM_ALERT);
        this.sourceServiceName = sourceServiceName;
        this.operation = operation;
        this.dependency = dependency;
        this.severity = severity;
        this.message = message;
        this.technicalDetails = technicalDetails;
    }

    public String getSourceServiceName() {
        return sourceServiceName;
    }

    public void setSourceServiceName(String sourceServiceName) {
        this.sourceServiceName = sourceServiceName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTechnicalDetails() {
        return technicalDetails;
    }

    public void setTechnicalDetails(String technicalDetails) {
        this.technicalDetails = technicalDetails;
    }
}
