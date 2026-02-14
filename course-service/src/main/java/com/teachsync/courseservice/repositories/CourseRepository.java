package com.teachsync.courseservice.repositories;

import com.teachsync.courseservice.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query(value = "select * from courses where teacher_id = :teacher_id", nativeQuery = true)
    List<Course> getAllByTeacher(@Param("teacher_id") Long teacherId);

    @Query(value = "select * from courses c" +
            " join course_topics ct on ct.course_id = c.id " +
            " join group_courses gc on gc.course_id = c.id " +
            " where c.id = :course_id", nativeQuery = true)
    Course getCourseWithFullData(@Param("course_id") Long courseId);

    @Modifying
    @Query(nativeQuery = true, value = "insert into COURSE_TOPICS ( COURSE_ID, TOPIC_ID ) " +
            "values ( :course_id, :topic_id )")
    void assignTopicToCourse(@Param("course_id") Long course_id, @Param("topic_id") Long topic_id );
}
