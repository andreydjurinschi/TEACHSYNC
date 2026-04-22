package com.teachsync.repositories;

import com.teachsync.domain.Schedule;
import com.teachsync.domain.WeekDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query(nativeQuery = true,
            value = """
                        select sc.*
                        from SCHEDULES sc
                        left join CLASS_ROOMS cl on cl.ID = sc.CLASS_ROOM_ID
                    """)
    List<Schedule> findWithClassRooms();

    @Query(value = """
                SELECT s.* 
                FROM schedules s
                JOIN schedule_days_info d ON d.schedule_id = s.id
                WHERE d.weekday = :day
                  AND s.start_time < :end
                  AND s.end_time > :start
            """, nativeQuery = true)
    List<Schedule> findConflictingSchedules(
            @Param("day") String day,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end
    );

    @Query("""
                SELECT DISTINCT s FROM Schedule s
                JOIN s.weekDays wd
                WHERE wd IN :days
                AND s.startTime < :endTime
                AND s.endTime > :startTime
                AND s.classRoom.id = :classRoomId
            """)
    List<Schedule> findClassRoomConflicts(
            @Param("days") Set<WeekDays> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("classRoomId") Long classRoomId
    );

    @Query("""
                SELECT DISTINCT s FROM Schedule s
                JOIN s.weekDays wd
                WHERE wd IN :days
                AND s.startTime < :endTime
                AND s.endTime > :startTime
            """)
    List<Schedule> findAllConflictingSchedules(
            @Param("days") Set<WeekDays> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("""
                SELECT DISTINCT s FROM Schedule s
                JOIN s.weekDays wd
                WHERE wd IN :days
                AND s.startTime < :endTime
                AND s.endTime > :startTime
                AND s.teacherId = :teacherId
            """)
    List<Schedule> findTeacherConflicts(
            @Param("days") Set<WeekDays> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("teacherId") Long teacherId
    );

    @Query("""
    SELECT DISTINCT s FROM Schedule s
    LEFT JOIN FETCH s.classRoom
    WHERE s.teacherId = :teacherId
""")
    List<Schedule> findAllForTeacher(@Param("teacherId") Long teacherId);

    //todo
/*    @Query("""
                    select distinct s from Schedule s
                    join s.weekDays wd
                    where wd in :days
                    and s.startTime < :endTime
                    and s.endTime > :startTime
                    and s.groupCourseId = :groupCourseId
            """)
    List<Schedule> findGroupCourseConflicts(
            @Param("days") Set<WeekDays> days,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("groupCourseId") Long groupCourseId
    );*/
}


