package com.teachsync.courseservice.services.domain;

import com.teachsync.courseservice.domain.Course;
import com.teachsync.courseservice.dto_s.courses.CourseDetailedDto;
import com.teachsync.courseservice.interaction.feign.clients.UserClient;
import com.teachsync.courseservice.mappers.CourseMapper;
import com.teachsync.courseservice.repositories.CourseRepository;
import com.teachsync.courseservice.interaction.feign.requests.TeacherCheckResponse;
import com.teachsync.courseservice.dto_s.courses.CourseUpdateDto;
import com.teachsync.courseservice.dto_s.courses.CourseBaseDto;
import com.teachsync.courseservice.dto_s.courses.CourseCreateDto;
import com.teachsync.courseservice.repositories.TopicRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository repository;
    private final TopicRepository topicRepository;
    private final UserClient userClient;

    @Autowired
    public CourseService(CourseRepository repository, TopicRepository topicRepository, UserClient userClient) {
        this.repository = repository;
        this.topicRepository = topicRepository;
        this.userClient = userClient;
    }

    public List<CourseBaseDto> findAll(){
        List<Course> courses = repository.findAll();
        return courses.stream().map(CourseMapper::mapToBaseDto).collect(Collectors.toList());
    }

    public CourseBaseDto findById(Long id){
        Course course = getCourse(id);
        return CourseMapper.mapToBaseDto(course);
    }

    @Transactional
    public void createCourse(CourseCreateDto dto){
        Course course = CourseMapper.mapToEntity(dto);
        repository.save(course);
    }

    @Transactional
    public void updateCourse(Long id, CourseUpdateDto dto){
       Course course = getCourse(id);
        if(StringUtils.hasText(dto.getName())){
            course.setName(dto.getName());
        }
        if(StringUtils.hasText(dto.getDescription())){
            course.setDescription(dto.getDescription());
        }
        if(StringUtils.hasText(dto.getPhotoUrl())){
            course.setPhotoUrl(dto.getPhotoUrl());
        }
    }

    @Transactional
    public void deleteCourse(Long id){
        Course course = getCourse(id);
        repository.delete(course);
    }

    @Transactional
    public void assignTopicToCourse(Long courseId, Long topicId){
        repository.findById(courseId).orElseThrow(() -> new NoSuchElementException("this course does not exist"));
        topicRepository.findById(topicId).orElseThrow(() -> new NoSuchElementException("this topic does not exist"));
        repository.assignTopicToCourse(courseId, topicId);
    }

    public CourseDetailedDto getAllCourseData(Long id){
        Course course = repository.getCourseWithFullData(id);
        if(course==null){
            throw new NoSuchElementException("this course does not exist");
        }
        return CourseMapper.mapToDetailedDto(course);
    }

    // interaction consumer
    @Transactional
    public void assignTeacherToCourse(Long courseId, Long userId) {
        Course course = repository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("course not found: " + courseId));

        TeacherCheckResponse response = userClient.isTeacher(userId);
        if (response == null || !response.isTeacher()) {
            throw new IllegalArgumentException("this user is not a teacher");
        }
        course.setTeacherId(userId);
    }

    // interaction producer

    public List<CourseBaseDto> getAllForUser(Long userId){
        List<Course> courses = repository.getAllByTeacher(userId);
        return courses
                .stream().map(CourseMapper::mapToBaseDto).toList();
    }

    private Course getCourse(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("this course does not exist"));
    }


}
