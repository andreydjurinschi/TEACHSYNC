package com.teachsync.services.domain;

import com.teachsync.domain.Course;
import com.teachsync.domain.Group;
import com.teachsync.dto_s.internal.ScheduleCleanupRequest;
import com.teachsync.dto_s.courses.CourseShortDto;
import com.teachsync.dto_s.groups.GroupBaseDto;
import com.teachsync.dto_s.groups.GroupCreateDto;
import com.teachsync.dto_s.groups.GroupUpdateDto;
import com.teachsync.dto_s.groups.GroupWithCoursesDto;
import com.teachsync.mappers.CourseMapper;
import com.teachsync.mappers.GroupMapper;
import com.teachsync.repositories.CourseRepository;
import com.teachsync.repositories.GroupRepository;
import com.teachsync.teachsyncevents.courses.GroupDeletedEvent;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository repository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;
    private final com.teachsync.interaction.kafka.CourseEventProducer courseEventProducer;

    public GroupService(GroupRepository repository, CourseRepository courseRepository, CourseService courseService, com.teachsync.interaction.kafka.CourseEventProducer courseEventProducer) {
        this.repository = repository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
        this.courseEventProducer = courseEventProducer;
    }

    public List<GroupBaseDto> getAll(){
        return repository.findAll().stream().map(GroupMapper::mapToBaseDto).collect(Collectors.toList());
    }

    public GroupBaseDto getById(Long id){
        return repository.findById(id).map(GroupMapper::mapToBaseDto).orElseThrow(() -> new NoSuchElementException("Group with id " + id + " does not exist"));
    }

    @Transactional
    public void update(Long id, GroupUpdateDto dto){
        Group group = getGroup(id);
        if(StringUtils.hasText(dto.getName())){
            group.setName(dto.getName());
        }
        if(dto.getOpenDate() != null){
            group.setDate(dto.getOpenDate());
        }
        if(dto.getCapacity() != null){
            group.setCapacity(dto.getCapacity());
        }
    }


    @Transactional
    public void create(GroupCreateDto dto){
        Group group = GroupMapper.mapToEntity(dto);
        repository.save(group);
    }

    @Transactional
    public void assignGroupToCourse(Long groupId, Long courseId) {
        getGroup(groupId);
        courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("this course does not exist"));
        repository.assignGroupToCourse(groupId, courseId);
    }

    public void delete(Long id, Long changedByUserId, String changedByName){
        Group group = getGroup(id);
        courseService.cleanupSchedules(
                courseRepository.findGroupCourseIdsByGroupId(id),
                "Группа закрыта и удалена из системы",
                changedByUserId,
                changedByName
        );
        courseRepository.deleteAllGroupRelationsForGroup(id);
        repository.delete(group);
        courseEventProducer.publishGroupDeleted(new GroupDeletedEvent(
                group.getId(),
                group.getName(),
                changedByUserId,
                changedByName
        ));
    }

    public GroupWithCoursesDto getDetailedDto(Long id){
        Group group = repository.findWithCourses(id);
        if(group == null){
            throw new NoSuchElementException("this group does not exist");
        }
        Set<Course> groupCourses = group.getCourses();
        Set<CourseShortDto> shortDtosSet = new HashSet<>();
        if(!groupCourses.isEmpty()){
            shortDtosSet = groupCourses.stream().map(CourseMapper::mapToShortDto).collect(Collectors.toSet());
        }

        GroupWithCoursesDto dto = GroupMapper.mapToDetailedDto(group);
        dto.setCourses(shortDtosSet);
        return dto;
    }

    @Transactional
    public void unassignGroupFromCourse(Long groupId, Long courseId) {
        getGroup(groupId);
        courseRepository.findById(courseId).orElseThrow(() -> new NoSuchElementException("this course does not exist"));

        repository.unassignGroupToCourse(groupId, courseId);
    }


    private Group getGroup(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("this group does not exist"));
    }
}
