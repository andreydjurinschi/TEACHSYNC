package com.teachsync.services.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.auth.service.JwtService;
import com.teachsync.domain.Category;
import com.teachsync.domain.Course;
import com.teachsync.domain.Group;
import com.teachsync.domain.Topic;
import com.teachsync.dto_s.courses.CourseDetailedDto;
import com.teachsync.dto_s.courses.CourseWithGroupDto;
import com.teachsync.dto_s.feign.CourseWithTeacherRequest;
import com.teachsync.interaction.feign.clients.UserClient;
import com.teachsync.interaction.feign.requests.TeacherRequest;
import com.teachsync.interaction.kafka.CourseEventProducer;
import com.teachsync.mappers.CourseMapper;
import com.teachsync.repositories.CategoryRepository;
import com.teachsync.repositories.CourseRepository;
import com.teachsync.interaction.feign.requests.TeacherCheckRequest;
import com.teachsync.dto_s.courses.CourseUpdateDto;
import com.teachsync.dto_s.courses.CourseBaseDto;
import com.teachsync.dto_s.courses.CourseCreateDto;
import com.teachsync.repositories.GroupRepository;
import com.teachsync.repositories.TopicRepository;
import com.teachsync.teachsyncevents.courses.CourseCreatedEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupEnrolledEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupRelationRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignmentRequestedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherUnassignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicsAddedEvent;
import com.teachsync.teachsyncevents.courses.CourseUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final Logger logger = Logger.getLogger(CourseService.class.getName());

    private final CourseRepository repository;
    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final UserClient userClient;
    private final CourseEventProducer courseEventProducer;
    private final ObjectMapper objectMapper;

    @Autowired
    public CourseService(CourseRepository repository, TopicRepository topicRepository, CategoryRepository categoryRepository, GroupRepository groupRepository, UserClient userClient, CourseEventProducer courseEventProducer, JwtService jwtService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.topicRepository = topicRepository;
        this.categoryRepository = categoryRepository;
        this.groupRepository = groupRepository;
        this.userClient = userClient;
        this.courseEventProducer = courseEventProducer;
        this.objectMapper = objectMapper;
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
        course.setPhotoUrl(normalizePhoto(dto.getPhotoUrl()));
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            course.setCategory(category);
        }
        repository.save(course);

        courseEventProducer.publishCourseCreated(new CourseCreatedEvent(
                course.getId(),
                course.getName(),
                course.getTeacherId()
        ));
    }

    @Transactional
    public void updateCourse(Long id, CourseUpdateDto dto) {
       Course course = getCourse(id);
       String previousState = course.toString();
        if(StringUtils.hasText(dto.getName())){
            course.setName(dto.getName());
        }
        if(StringUtils.hasText(dto.getDescription())){
            course.setDescription(dto.getDescription());
        }
        if(dto.getPhotoUrl() != null){
            course.setPhotoUrl(normalizePhoto(dto.getPhotoUrl()));
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            course.setCategory(category);
        }
        String newState =  course.toString();

        courseEventProducer.publishCourseEdited(new CourseUpdatedEvent(
                course.getId(), previousState, newState
        ));
    }

    @Transactional
    public void deleteCourse(Long id){
        Course course = getCourse(id);
        repository.delete(course);
    }

    @Transactional
    public void assignTopicToCourse(Long courseId, Long topicId){
        Course course = repository.findById(courseId).orElseThrow(() -> new NoSuchElementException("this course does not exist"));
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new NoSuchElementException("this topic does not exist"));
        repository.assignTopicToCourse(courseId, topicId);

        courseEventProducer.publishCourseTopicAdded(new CourseTopicsAddedEvent(
                courseId, topicId, course.getName(), topic.getName(), course.getTeacherId()
        ));
    }

    @Transactional
    public void assignGroupToCourse(Long courseId, Long groupId){
        Course course = repository.findById(courseId).orElseThrow(() -> new NoSuchElementException("this course does not exist"));
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("this group does not exist"));
        repository.assignGroupToCourse(courseId, groupId);

        courseEventProducer.publishCourseGroupEnrolled(new CourseGroupEnrolledEvent(
                courseId, groupId, course.getName(), group.getName(), course.getTeacherId()
        ));
    }

    @Transactional
    public void unassignTopicToCourse(Long courseId, Long topicId){
        Course course = repository.findById(courseId).orElseThrow(() -> new NoSuchElementException("this course does not exist"));
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new NoSuchElementException("this topic does not exist"));
        repository.unassignTopicToCourse(courseId, topicId);

        courseEventProducer.publishCourseTopicRemoved(new CourseTopicRemovedEvent(
                courseId, topicId, course.getName(), topic.getName(), course.getTeacherId()
        ));
    }

    @Transactional
    public void unassignGroupToCourse(Long courseId, Long groupId){
        Course course = repository.findById(courseId).orElseThrow(() -> new NoSuchElementException("this course does not exist"));
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("this group does not exist"));
        repository.unassignGroupToCourse(courseId, groupId);

        courseEventProducer.publishCourseGroupRemoved(new CourseGroupRelationRemovedEvent(
                courseId, groupId, course.getName(), group.getName(), course.getTeacherId()
        ));
    }

    public CourseDetailedDto getAllCourseData(Long id){
        Course course = repository.getCourseWithFullData(id);
        if(course==null){
            throw new NoSuchElementException("this course does not exist");
        }
        return CourseMapper.mapToDetailedDto(course);
    }

    public CourseWithGroupDto getCourseWithGroup(Long id){
        Course course = repository.getCourseWithGroups(id);
        return CourseMapper.mapToCourseWithGroupDto(course);
    }

    // interaction consumer
    @Transactional
    public void assignTeacherToCourse(Long courseId, Long userId) {
        Course course = repository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("course not found: " + courseId));

        validateTeacherCanLeadCourse(course, userId);
        course.setTeacherId(userId);

        courseEventProducer.publishCourseTeacherAssigned(
                new CourseTeacherAssignedEvent(
                      course.getId(), course.getName(), userId
                )
        );
    }

    @Transactional
    public void requestTeacherForCourse(Long courseId, Long teacherId) {
        Course course = getCourse(courseId);
        if (course.getTeacherId() != null) {
            throw new IllegalArgumentException("course already has a teacher");
        }
        validateTeacherCanLeadCourse(course, teacherId);
        Category category = course.getCategory();
        courseEventProducer.publishCourseTeacherAssignmentRequested(
                new CourseTeacherAssignmentRequestedEvent(
                        course.getId(),
                        course.getName(),
                        teacherId,
                        category == null ? null : category.getId(),
                        category == null ? null : category.getName()
                )
        );
    }

    @Transactional
    public void approveTeacherAssignment(Long courseId, Long teacherId) {
        Course course = getCourse(courseId);
        if (course.getTeacherId() != null) {
            throw new IllegalArgumentException("course already has a teacher");
        }
        validateTeacherCanLeadCourse(course, teacherId);
        course.setTeacherId(teacherId);
        courseEventProducer.publishCourseTeacherAssigned(
                new CourseTeacherAssignedEvent(
                        course.getId(), course.getName(), teacherId
                )
        );
    }

    @Transactional
    public void unassignTeacherFromCourse(Long courseId) {
        Course course = repository.getCourseWithFullData(courseId);
        Long previousTeacherId = course.getTeacherId();
        course.setTeacherId(null);
        publishCourseTeacherUnassigned(course, previousTeacherId, "Учитель снят с курса вручную");
    }

    @Transactional
    public int unassignTeacherFromAllCourses(Long teacherId) {
        List<Course> courses = repository.getAllByTeacher(teacherId);
        courses.forEach(course -> {
            course.setTeacherId(null);
            publishCourseTeacherUnassigned(course, teacherId, "У пользователя изменилась роль или учетная запись удалена");
        });
        return courses.size();
    }

    public CourseWithTeacherRequest getCourseWithTeacher(Long id){
        Course course = getCourse(id);
        Long teacherId = course.getTeacherId();
        TeacherCheckRequest response = userClient.isTeacher(teacherId);
        if (response == null || !response.isTeacher()) {
            throw new IllegalArgumentException("this user is not a teacher");
        }
        TeacherRequest teacherRequest = userClient.getTeacher(teacherId);

        return new CourseWithTeacherRequest(
                course.getName(), course.getDescription(), teacherRequest
        );
    }

    // interaction producer
    public List<CourseBaseDto> getAllForUser(Long userId){
        List<Course> courses = repository.getAllByTeacher(userId);
        String joined = courses.stream().map(Course::toString).collect(Collectors.joining("\n"));
        logger.info("Courses for user " + userId + ": " + joined);
        return courses
                .stream().map(CourseMapper::mapToBaseDto).toList();
    }

    public List<CourseDetailedDto> getCoursesFullDataForTeacher(Long teacherId) {
        List<Course> courses = repository.getAllByTeacher(teacherId);
        String joined = courses.stream().map(Course::toString).collect(Collectors.joining("\n"));
        logger.info("Courses for user " + teacherId + ": " + joined);
        return courses
                .stream().map(CourseMapper::mapToDetailedDto).toList();
    }

    private Course getCourse(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("this course does not exist"));
    }

    private void validateTeacherCanLeadCourse(Course course, Long teacherId) {
        TeacherCheckRequest response = userClient.isTeacher(teacherId);
        if (response == null || !response.isTeacher()) {
            throw new IllegalArgumentException("this user is not a teacher");
        }
        Category category = course.getCategory();
        if (category == null) {
            return;
        }
        TeacherRequest teacher = userClient.getTeacher(teacherId);
        boolean hasRequiredCategory = teacher.specializations() != null
                && teacher.specializations().stream().anyMatch(s -> category.getId().equals(s.getId()));
        if (!hasRequiredCategory) {
            throw new IllegalArgumentException("teacher does not have required course category");
        }
    }

    private void publishCourseTeacherUnassigned(Course course, Long previousTeacherId, String reason) {
        Category category = course.getCategory();
        courseEventProducer.publishCourseTeacherUnassigned(
                new CourseTeacherUnassignedEvent(
                        course.getId(),
                        course.getName(),
                        previousTeacherId,
                        category == null ? null : category.getId(),
                        category == null ? null : category.getName(),
                        reason
                )
        );
    }

    private String normalizePhoto(String photoUrl) {
        if (!StringUtils.hasText(photoUrl)) {
            return null;
        }
        return photoUrl.trim();
    }

}
