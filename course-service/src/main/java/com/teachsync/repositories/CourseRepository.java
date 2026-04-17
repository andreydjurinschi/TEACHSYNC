package com.teachsync.repositories;

import com.teachsync.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query(value = "select c.* from courses c where c.teacher_id = :teacher_id", nativeQuery = true)
    List<Course> getAllByTeacher(@Param("teacher_id") Long teacherId);

    @Query(value = "select c.* from courses c" +
            " left join course_topics ct on ct.course_id = c.id " +
            " left join group_courses gc on gc.course_id = c.id " +
            " where c.id = :course_id", nativeQuery = true)
    Course getCourseWithFullData(@Param("course_id") Long courseId);

    @Query(
            nativeQuery = true,
            value = "select c.* from courses c left join group_courses gc on c.id = gc.course_id " +
                    "where c.id = :course_id"
    )
    Course getCourseWithGroups(@Param("course_id") Long course_id);

    @Modifying
    @Query(nativeQuery = true, value = "insert into COURSE_TOPICS ( COURSE_ID, TOPIC_ID ) " +
            "values ( :course_id, :topic_id )")
    void assignTopicToCourse(@Param("course_id") Long course_id, @Param("topic_id") Long topic_id );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = "delete from COURSE_TOPICS " +
            "where COURSE_ID = :course_id and TOPIC_ID = :topic_id")
    void unassignTopicToCourse(@Param("course_id") Long course_id, @Param("topic_id") Long topic_id);

    @Modifying
    @Query(nativeQuery = true, value = "insert into group_courses ( course_id, group_id ) " +
            "values ( :course_id, :group_id )")
    void assignGroupToCourse(@Param("course_id") Long course_id, @Param("group_id") Long group_id );

    @Modifying
    @Query(nativeQuery = true, value = "delete from group_courses " +
            "where group_id=:group_id and course_id=:course_id")
    void unassignGroupToCourse(@Param("course_id") Long course_id, @Param("group_id") Long group_id );


}
