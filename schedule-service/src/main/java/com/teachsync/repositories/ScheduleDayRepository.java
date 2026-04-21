package com.teachsync.repositories;

import com.teachsync.domain.ScheduleDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleDayRepository extends JpaRepository<ScheduleDay, Long> {
}
