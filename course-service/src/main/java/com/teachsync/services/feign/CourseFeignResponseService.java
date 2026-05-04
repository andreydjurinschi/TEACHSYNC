package com.teachsync.services.feign;

import com.teachsync.domain.Course;
import com.teachsync.domain.Group;
import com.teachsync.domain.GroupCourse;
import com.teachsync.interaction.feign.requests.TeacherRequest;
import com.teachsync.interaction.feign.responses.GroupCourseResponseForScheduleService;
import com.teachsync.repositories.CourseRepository;
import com.teachsync.repositories.GroupCourseRepository;
import com.teachsync.repositories.GroupRepository;
import com.teachsync.services.feign.groupcourse.GroupCourseSizeDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseFeignResponseService {

    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final GroupCourseRepository groupCourseRepository;
    private final ReferenceDataCacheService referenceDataCacheService;

    public CourseFeignResponseService(GroupRepository groupRepository, CourseRepository courseRepository, GroupCourseRepository groupCourseRepository, ReferenceDataCacheService referenceDataCacheService) {
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.groupCourseRepository = groupCourseRepository;
        this.referenceDataCacheService = referenceDataCacheService;
    }

    public List<GroupCourseResponseForScheduleService> getAll() {
        List<GroupCourse> groupCourses = groupCourseRepository.findAll();

        List<Long> teacherIds = groupCourses.stream()
                .map(gc -> gc.getCourse().getTeacherId())
                .filter(id -> id != null)
                .distinct()
                .toList();

        System.out.println(">>> teacherIds: " + teacherIds); // ← добавь

        List<TeacherRequest> teachers = referenceDataCacheService.getTeachersByIds(teacherIds);
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
                            gc.getCourse().getCategory() == null ? null : gc.getCourse().getCategory().getId(),
                            gc.getCourse().getCategory() == null ? null : gc.getCourse().getCategory().getName(),
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

        TeacherRequest teacher = course.getTeacherId() == null ? null : referenceDataCacheService.getTeacher(course.getTeacherId());

        return new GroupCourseResponseForScheduleService(
                groupCourse.getId(), group.getId(), course.getId(),
                group.getName(), course.getName(),
                course.getCategory() == null ? null : course.getCategory().getId(),
                course.getCategory() == null ? null : course.getCategory().getName(),
                course.getTeacherId(),
                teacher == null ? "—" : teacher.name() + " " + teacher.surname()
        );
    }

    public List<GroupCourseResponseForScheduleService> findAllByIds(List<Long> ids) {
        List<GroupCourse> groupCourses = groupCourseRepository.findAllById(ids);

        List<Long> teacherIds = groupCourses.stream()
                .map(gc -> gc.getCourse().getTeacherId())
                .filter(id -> id != null)
                .distinct()
                .toList();

        Map<Long, TeacherRequest> teacherMap = referenceDataCacheService.getTeachersByIds(teacherIds)
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
                            gc.getCourse().getCategory() == null ? null : gc.getCourse().getCategory().getId(),
                            gc.getCourse().getCategory() == null ? null : gc.getCourse().getCategory().getName(),
                            teacherId, teacherName
                    );
                })
                .toList();
    }

    public GroupCourseSizeDto getGroupDtoWithSize(Long id){
        GroupCourse groupCourse = groupCourseRepository.findById(id).orElseThrow();
        Group group = groupCourse.getGroup();
        Course course = groupCourse.getCourse();
        GroupCourseSizeDto groupCourseSizeDto = new GroupCourseSizeDto();
        groupCourseSizeDto.setId(groupCourse.getId());
        groupCourseSizeDto.setGroupId(group.getId());
        groupCourseSizeDto.setCourseId(course.getId());
        groupCourseSizeDto.setCapacity(group.getCapacity());

        return groupCourseSizeDto;
    }


}
