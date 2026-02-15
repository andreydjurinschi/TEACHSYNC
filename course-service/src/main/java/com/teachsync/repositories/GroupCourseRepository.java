package com.teachsync.repositories;

import com.teachsync.domain.GroupCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCourseRepository extends JpaRepository<GroupCourse, Long> {

}
