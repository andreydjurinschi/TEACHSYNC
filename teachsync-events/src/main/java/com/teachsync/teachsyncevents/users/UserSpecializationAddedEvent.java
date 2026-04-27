package com.teachsync.teachsyncevents.users;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class UserSpecializationAddedEvent extends BaseEvent {
    private Long userId;
    private String specializationName;

    public UserSpecializationAddedEvent() {
    }

    public UserSpecializationAddedEvent(Long userId, String specializationName) {
        super("user-service", ActionTypes.USER_SPEC_ADDED);
        this.userId = userId;
        this.specializationName = specializationName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }
}
