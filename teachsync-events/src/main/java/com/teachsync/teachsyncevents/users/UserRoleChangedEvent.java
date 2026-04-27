package com.teachsync.teachsyncevents.users;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class UserRoleChangedEvent extends BaseEvent {

    private Long userId;
    private String previousRole;
    private String newRole;

    public UserRoleChangedEvent( Long userId, String previousRole, String newRole) {
        super("user-service", ActionTypes.USER_ROLE_CHANGED);
        this.userId = userId;
        this.previousRole = previousRole;
        this.newRole = newRole;
    }

    public UserRoleChangedEvent() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPreviousRole() {
        return previousRole;
    }

    public void setPreviousRole(String previousRole) {
        this.previousRole = previousRole;
    }

    public String getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }
}
