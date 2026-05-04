package com.teachsync.teachsyncevents.constants;

public class ActionTypes {
    // courses
    public static final String COURSE_CREATED = "COURSE_CREATED";
    public static final String COURSE_DELETED = "COURSE_DELETED";
    public static final String GROUP_DELETED = "GROUP_DELETED";
    public static final String COURSE_TEACHER_ASSIGNED = "COURSE_TEACHER_ASSIGNED";
    public static final String COURSE_EDITED = "COURSE_EDITED";
    public static final String COURSE_GROUP_ENROLLED = "COURSE_GROUP_ENROLLED";
    public static final String COURSE_GROUP_REMOVED = "COURSE_GROUP_REMOVED";
    public static final String COURSE_TOPIC_ADDED = "COURSE_TOPIC_ADDED";
    public static final String COURSE_TOPIC_REMOVED = "COURSE_TOPIC_REMOVED";
    public static final String COURSE_TEACHER_UNASSIGNED = "COURSE_TEACHER_UNASSIGNED";
    public static final String COURSE_TEACHER_ASSIGNMENT_REQUESTED = "COURSE_TEACHER_ASSIGNMENT_REQUESTED";

    // schedules
    public static final String SCHEDULE_CREATED = "SCHEDULE_CREATED";
    public static final String SCHEDULE_UPDATED = "SCHEDULE_UPDATED";
    public static final String SCHEDULE_DELETED = "SCHEDULE_DELETED";

    // replacements
    public static final String REPLACEMENT_REQUESTED = "REPLACEMENT_REQUESTED";
    public static final String REPLACEMENT_APPROVED = "REPLACEMENT_APPROVED";
    public static final String REPLACEMENT_STATUS_CHANGED = "REPLACEMENT_STATUS_CHANGED";

    // users
    public static final String USER_ROLE_CHANGED = "USER_ROLE_CHANGED";
    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_DELETED = "USER_DELETED";
    public static final String USER_SPEC_ADDED = "USER_SPECIALIZATION_ADDED";
    public static final String USER_SPEC_DELETED = "USER_SPECIALIZATION_DELETED";

    // system
    public static final String SYSTEM_ALERT = "SYSTEM_ALERT";
}
