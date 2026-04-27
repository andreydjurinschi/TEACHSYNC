package com.teachsync.teachsyncevents.users;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class UserCreatedEvent extends BaseEvent {

    private Long userId;
    private String userRole;
    private String userEmail;
    private String firstName;
    private String lastName;

    public UserCreatedEvent(Long userId, String userRole, String userEmail, String firstName, String lastName) {
        super("user-service", ActionTypes.USER_CREATED);
        this.userId = userId;
        this.userRole = userRole;
        this.userEmail = userEmail;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserCreatedEvent() {
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
