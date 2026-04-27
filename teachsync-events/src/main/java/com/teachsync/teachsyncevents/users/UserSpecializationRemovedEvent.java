package com.teachsync.teachsyncevents.users;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class UserSpecializationRemovedEvent extends BaseEvent {

    private Long userId;
    private String categoryName;


    public UserSpecializationRemovedEvent(Long userId, String categoryName) {
        super("user-service", ActionTypes.USER_SPEC_DELETED);
        this.userId = userId;
        this.categoryName = categoryName;
    }

    public UserSpecializationRemovedEvent() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
