package com.teachsync.repositories;

import com.teachsync.domain.Schedule;
import com.teachsync.domain.WeekDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

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
}


