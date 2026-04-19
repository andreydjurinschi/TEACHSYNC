package com.teachsync.services.feign;

import com.teachsync.domain.Course;
import com.teachsync.domain.Group;
import com.teachsync.domain.GroupCourse;
import com.teachsync.interaction.feign.responses.GroupCourseResponseForScheduleService;
import com.teachsync.repositories.CourseRepository;
import com.teachsync.repositories.GroupCourseRepository;
import com.teachsync.repositories.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<GroupCourseResponseForScheduleService> getAll() {
        return groupCourseRepository.findAll()
                .stream()
                .map(gc -> new GroupCourseResponseForScheduleService(
                        gc.getId(), gc.getGroup().getId(), gc.getCourse().getId(),
                        gc.getGroup().getName(), gc.getCourse().getName()
                ))
                .toList();
    }

    public GroupCourseResponseForScheduleService getGroupCourse(Long groupCourseId){
        GroupCourse groupCourse = groupCourseRepository.findById(groupCourseId)
                .orElseThrow();
        Course course = groupCourse.getCourse();
        Group group = groupCourse.getGroup();

        return new GroupCourseResponseForScheduleService(
                groupCourse.getId(),group.getId(), course.getId(), group.getName(), course.getName()
        );
    }

    public List<GroupCourseResponseForScheduleService> findAllByIds(List<Long> ids) {
        return groupCourseRepository.findAllById(ids)
                .stream()
                .map(gc -> new GroupCourseResponseForScheduleService(
                        gc.getId(),gc.getGroup().getId(),gc.getCourse().getId(),
                        gc.getGroup().getName(),
                        gc.getCourse().getName()
                ))
                .toList();
    }


}
