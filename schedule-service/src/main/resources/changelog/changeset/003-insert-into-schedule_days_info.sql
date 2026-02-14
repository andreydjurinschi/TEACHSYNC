INSERT INTO schedule_days_info (schedule_id, weekday)
VALUES
    ((SELECT id FROM schedules WHERE teacher_id = 2 AND group_course_id = 2), 'MON'),
    ((SELECT id FROM schedules WHERE teacher_id = 2 AND group_course_id = 2), 'SAT');
