package com.teachsync.teachsyncevents.base;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEvent {

    private String uuid = UUID.randomUUID().toString();
    private String serviceName;
    private String actionType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt = LocalDateTime.now();

    public BaseEvent(String serviceName, String actionType) {
        this.serviceName = serviceName;
        this.occurredAt = LocalDateTime.now();
        this.uuid = UUID.randomUUID().toString();
        this.actionType = actionType;
    }

    public BaseEvent() {}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
