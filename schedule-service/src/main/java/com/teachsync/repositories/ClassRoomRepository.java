package com.teachsync.repositories;

import com.teachsync.domain.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {

    @Query(nativeQuery = true, value = "select cr.* from CLASS_ROOMS cr" +
            " left join SCHEDULES s on s.CLASS_ROOM_ID = cr.ID where cr.ID = :id")
    ClassRoom findWithSchedules(@Param("id") Long classRoomId);
}
