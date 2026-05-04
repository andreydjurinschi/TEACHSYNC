package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class GroupDeletedEvent extends BaseEvent {

    private Long groupId;
    private String groupName;
    private Long changedByUserId;
    private String changedByName;

    public GroupDeletedEvent() {
    }

    public GroupDeletedEvent(Long groupId, String groupName, Long changedByUserId, String changedByName) {
        super("course-service", ActionTypes.GROUP_DELETED);
        this.groupId = groupId;
        this.groupName = groupName;
        this.changedByUserId = changedByUserId;
        this.changedByName = changedByName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }
}
