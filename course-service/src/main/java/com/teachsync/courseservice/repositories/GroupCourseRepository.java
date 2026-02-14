package com.teachsync.courseservice.repositories;

import com.teachsync.courseservice.domain.GroupCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCourseRepository extends JpaRepository<GroupCourse, Long> {

}
