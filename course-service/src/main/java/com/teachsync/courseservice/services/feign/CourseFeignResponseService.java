package com.teachsync.courseservice.services.feign;

import com.teachsync.courseservice.domain.Course;
import com.teachsync.courseservice.domain.Group;
import com.teachsync.courseservice.domain.GroupCourse;
import com.teachsync.courseservice.interaction.feign.responses.GroupCourseResponseForScheduleService;
import com.teachsync.courseservice.repositories.CourseRepository;
import com.teachsync.courseservice.repositories.GroupCourseRepository;
import com.teachsync.courseservice.repositories.GroupRepository;
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
