package com.teachsync.courseservice.services.domain;

import com.teachsync.courseservice.domain.Course;
import com.teachsync.courseservice.domain.Group;
import com.teachsync.courseservice.dto_s.courses.CourseShortDto;
import com.teachsync.courseservice.dto_s.groups.GroupBaseDto;
import com.teachsync.courseservice.dto_s.groups.GroupCreateDto;
import com.teachsync.courseservice.dto_s.groups.GroupUpdateDto;
import com.teachsync.courseservice.dto_s.groups.GroupWithCoursesDto;
import com.teachsync.courseservice.mappers.CourseMapper;
import com.teachsync.courseservice.mappers.GroupMapper;
import com.teachsync.courseservice.repositories.CourseRepository;
import com.teachsync.courseservice.repositories.GroupRepository;
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

    public GroupService(GroupRepository repository, CourseRepository courseRepository) {
        this.repository = repository;
        this.courseRepository = courseRepository;
    }

    public List<GroupBaseDto> getAll(){
        return repository.findAll().stream().map(GroupMapper::mapToBaseDto).collect(Collectors.toList());
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

    public void delete(Long id){
        Group group = getGroup(id);
        repository.delete(group);
    }

    // relations
/*    public GroupWithCoursesDto getDetailedDto(Long id){
        Group group = getGroup(id);
        Set<Course> groupCourses = group.getCourses();
        Set<CourseShortDto> shortDtosSet = new HashSet<>();
        if(!groupCourses.isEmpty()){
            shortDtosSet = groupCourses.stream().map(CourseMapper::mapToShortDto).collect(Collectors.toSet());
        }
        GroupWithCoursesDto groupWithCoursesDto = GroupMapper.mapToDetailedDto(group);
        groupWithCoursesDto.setCourses(shortDtosSet);
        return groupWithCoursesDto;
    }*/

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


    private Group getGroup(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("this group does not exist"));
    }
}
