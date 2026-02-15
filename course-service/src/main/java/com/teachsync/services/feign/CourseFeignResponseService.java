package com.teachsync.services.feign;

import com.teachsync.domain.Course;
import com.teachsync.domain.Group;
import com.teachsync.domain.GroupCourse;
import com.teachsync.interaction.feign.responses.GroupCourseResponseForScheduleService;
import com.teachsync.repositories.CourseRepository;
import com.teachsync.repositories.GroupCourseRepository;
import com.teachsync.repositories.GroupRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseFeignResponseService {

    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final GroupCourseRepository groupCourseRepository;
    public CourseFeignResponseService(GroupRepository groupRepository, CourseRepository courseRepository, GroupCourseRepository groupCourseRepository) {
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.groupCourseRepository = groupCourseRepository;
    }

    public GroupCourseResponseForScheduleService getGroupCourse(Long groupCourseId){
        GroupCourse groupCourse = groupCourseRepository.findById(groupCourseId)
                .orElseThrow();
        Course course = groupCourse.getCourse();
        Group group = groupCourse.getGroup();

        return new GroupCourseResponseForScheduleService(
                group.getId(), course.getId(), group.getName(), course.getName()
        );
    }
}
