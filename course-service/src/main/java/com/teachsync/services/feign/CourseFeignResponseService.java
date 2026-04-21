package com.teachsync.services.feign;

import com.teachsync.domain.Course;
import com.teachsync.domain.Group;
import com.teachsync.domain.GroupCourse;
import com.teachsync.interaction.feign.clients.UserClient;
import com.teachsync.interaction.feign.requests.TeacherRequest;
import com.teachsync.interaction.feign.responses.GroupCourseResponseForScheduleService;
import com.teachsync.repositories.CourseRepository;
import com.teachsync.repositories.GroupCourseRepository;
import com.teachsync.repositories.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseFeignResponseService {

    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final GroupCourseRepository groupCourseRepository;
    private final UserClient userClient;

    public CourseFeignResponseService(GroupRepository groupRepository, CourseRepository courseRepository, GroupCourseRepository groupCourseRepository, UserClient userClient) {
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.groupCourseRepository = groupCourseRepository;
        this.userClient = userClient;
    }

    public List<GroupCourseResponseForScheduleService> getAll() {
        List<GroupCourse> groupCourses = groupCourseRepository.findAll();

        List<Long> teacherIds = groupCourses.stream()
                .map(gc -> gc.getCourse().getTeacherId())
                .distinct()
                .toList();

        System.out.println(">>> teacherIds: " + teacherIds); // ← добавь

        List<TeacherRequest> teachers = userClient.getTeachersByIds(teacherIds);
        System.out.println(">>> teachers from user-service: " + teachers); // ← добавь

        Map<Long, TeacherRequest> teacherMap = teachers.stream()
                .collect(Collectors.toMap(TeacherRequest::id, t -> t));

        return groupCourses.stream()
                .map(gc -> {
                    Long teacherId = gc.getCourse().getTeacherId();
                    TeacherRequest teacher = teacherMap.get(teacherId);
                    String teacherName = teacher != null
                            ? teacher.name() + " " + teacher.surname()
                            : "—";
                    return new GroupCourseResponseForScheduleService(
                            gc.getId(), gc.getGroup().getId(), gc.getCourse().getId(),
                            gc.getGroup().getName(), gc.getCourse().getName(),
                            teacherId, teacherName
                    );
                })
                .toList();
    }

    public GroupCourseResponseForScheduleService getGroupCourse(Long groupCourseId) {
        GroupCourse groupCourse = groupCourseRepository.findById(groupCourseId)
                .orElseThrow();
        Course course = groupCourse.getCourse();
        Group group = groupCourse.getGroup();

        TeacherRequest teacher = userClient.getTeacher(course.getTeacherId());

        return new GroupCourseResponseForScheduleService(
                groupCourse.getId(), group.getId(), course.getId(),
                group.getName(), course.getName(),
                course.getTeacherId(),
                teacher.name() + " " + teacher.surname()
        );
    }

    public List<GroupCourseResponseForScheduleService> findAllByIds(List<Long> ids) {
        List<GroupCourse> groupCourses = groupCourseRepository.findAllById(ids);

        List<Long> teacherIds = groupCourses.stream()
                .map(gc -> gc.getCourse().getTeacherId())
                .distinct()
                .toList();

        Map<Long, TeacherRequest> teacherMap = userClient.getTeachersByIds(teacherIds)
                .stream()
                .collect(Collectors.toMap(TeacherRequest::id, t -> t));

        return groupCourses.stream()
                .map(gc -> {
                    Long teacherId = gc.getCourse().getTeacherId();
                    TeacherRequest teacher = teacherMap.get(teacherId);
                    String teacherName = teacher != null
                            ? teacher.name() + " " + teacher.surname()
                            : "—";
                    return new GroupCourseResponseForScheduleService(
                            gc.getId(), gc.getGroup().getId(), gc.getCourse().getId(),
                            gc.getGroup().getName(), gc.getCourse().getName(),
                            teacherId, teacherName
                    );
                })
                .toList();
    }


}
