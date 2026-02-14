package org.cedacri.spring.scheduleservice.repositories;

import org.cedacri.spring.scheduleservice.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
