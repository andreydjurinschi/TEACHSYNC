package com.teachsync.dto_s.internal;

import java.util.List;

public class ScheduleCleanupRequest {

    private List<Long> groupCourseIds;
    private String reason;
    private Long changedByUserId;
    private String changedByName;

    public ScheduleCleanupRequest() {
    }

    public ScheduleCleanupRequest(List<Long> groupCourseIds, String reason, Long changedByUserId, String changedByName) {
        this.groupCourseIds = groupCourseIds;
        this.reason = reason;
        this.changedByUserId = changedByUserId;
        this.changedByName = changedByName;
    }

    public List<Long> getGroupCourseIds() {
        return groupCourseIds;
    }

    public void setGroupCourseIds(List<Long> groupCourseIds) {
        this.groupCourseIds = groupCourseIds;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
