-- changeset andrei:insert-schedule

INSERT INTO schedules (
    start_time,
    end_time,
    group_course_id,
    teacher_id,
    class_room_id
) VALUES (
             '11:00:00',
             '12:30:00',
             2,
             2,
             1
         );

