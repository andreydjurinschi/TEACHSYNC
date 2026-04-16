package com.teachsync.repositories;

import com.teachsync.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query(value = "select g.* from GROUPS g " +
            "left join PUBLIC.GROUP_COURSES GC on g.ID = GC.GROUP_ID " +
            "where g.ID = :group_id", nativeQuery = true)
    Group findWithCourses(@Param("group_id") Long group_id);

    @Modifying
    @Query(nativeQuery = true, value = "insert into GROUP_COURSES (group_id, course_id) VALUES ( :group_id, :course_id )")
    void assignGroupToCourse(@Param("group_id") Long groupId, @Param("course_id") Long courseId);

    @Modifying
    @Query(nativeQuery = true, value = "delete from GROUP_COURSES gc where gc.group_id = :group_id and gc.course_id = :course_id")
    void unassignGroupToCourse(@Param("group_id") Long groupId, @Param("course_id") Long courseId);



}
