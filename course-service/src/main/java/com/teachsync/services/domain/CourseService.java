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
import com.teachsync.dto_s.internal.ScheduleCleanupRequest;
import com.teachsync.dto_s.statistics.CourseStatisticsDto;
import com.teachsync.interaction.feign.clients.ScheduleCleanupClient;
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
import com.teachsync.teachsyncevents.courses.CourseDeletedEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupEnrolledEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupRelationRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherUnassignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicsAddedEvent;
import com.teachsync.teachsyncevents.courses.CourseUpdatedEvent;
import com.teachsync.teachsyncevents.system.SystemAlertEvent;
import com.teachsync.services.feign.ReferenceDataCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
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
    private final ReferenceDataCacheService referenceDataCacheService;
    private final CourseEventProducer courseEventProducer;
    private final ScheduleCleanupClient scheduleCleanupClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public CourseService(CourseRepository repository, TopicRepository topicRepository, CategoryRepository categoryRepository, GroupRepository groupRepository, UserClient userClient, ReferenceDataCacheService referenceDataCacheService, CourseEventProducer courseEventProducer, ScheduleCleanupClient scheduleCleanupClient, JwtService jwtService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.topicRepository = topicRepository;
        this.categoryRepository = categoryRepository;
        this.groupRepository = groupRepository;
        this.userClient = userClient;
        this.referenceDataCacheService = referenceDataCacheService;
        this.courseEventProducer = courseEventProducer;
        this.scheduleCleanupClient = scheduleCleanupClient;
        this.objectMapper = objectMapper;
    }

    public List<CourseBaseDto> findAll(){
        List<Course> courses = repository.findAll();
        return courses.stream().map(CourseMapper::mapToBaseDto).collect(Collectors.toList());
    }

    public CourseStatisticsDto getStatistics() {
        return new CourseStatisticsDto(
                repository.count(),
                repository.countWithTeacher(),
                repository.countWithoutTeacher(),
                repository.countGroupCourseRelations()
        );
    }

    public CourseBaseDto findById(Long id){
        Course course = getCourse(id);
        return CourseMapper.mapToBaseDto(course);
    }

    public void assertCanManageCourse(Long courseId, Long userId, String role) {
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            return;
        }
        Course course = getCourse(courseId);
        if ("TEACHER".equals(role) && userId != null && userId.equals(course.getTeacherId())) {
            return;
        }
        throw new AccessDeniedException("teacher can manage only assigned courses");
    }

    public void assertCanManageCourseGroups(String role) {
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            return;
        }
        throw new AccessDeniedException("only managers and admins can manage course groups");
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
        updateCourse(id, dto, null, null);
    }

    @Transactional
    public void updateCourse(Long id, CourseUpdateDto dto, Long changedByUserId, String changedByRole) {
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
                course.getId(),
                course.getName(),
                previousState,
                newState,
                changedByUserId,
                resolveUserName(changedByUserId),
                changedByRole
        ));
    }

    @Transactional
    public void deleteCourse(Long id, Long changedByUserId, String changedByName){
        Course course = getCourse(id);
        cleanupSchedules(
                repository.findGroupCourseIdsByCourseId(id),
                "Курс удален из системы",
                changedByUserId,
                changedByName
        );
        repository.deleteAllTopicRelations(id);
        repository.deleteAllGroupRelationsForCourse(id);
        repository.delete(course);
        courseEventProducer.publishCourseDeleted(new CourseDeletedEvent(
                course.getId(),
                course.getName(),
                course.getTeacherId(),
                course.getCategory() == null ? null : course.getCategory().getName(),
                changedByUserId,
                changedByName
        ));
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
        TeacherCheckRequest response = requireDependency(
                "Получение курса с преподавателем: проверка роли преподавателя",
                "users-service",
                () -> userClient.isTeacher(teacherId)
        );
        if (response == null || !response.isTeacher()) {
            throw new IllegalArgumentException("this user is not a teacher");
        }
        TeacherRequest teacherRequest = referenceDataCacheService.getTeacher(teacherId);

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

    @Transactional(readOnly = true)
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

    @Transactional
    public void cleanupSchedules(List<Long> groupCourseIds, String reason, Long changedByUserId, String changedByName) {
        if (groupCourseIds == null || groupCourseIds.isEmpty()) {
            return;
        }
        requireDependency(
                "Удаление связанных расписаний при закрытии курса или группы",
                "schedule-service",
                () -> {
                    scheduleCleanupClient.cleanupSchedulesByGroupCourses(
                            new ScheduleCleanupRequest(groupCourseIds, reason, changedByUserId, changedByName)
                    );
                    return null;
                }
        );
    }

    private void validateTeacherCanLeadCourse(Course course, Long teacherId) {
        TeacherCheckRequest response = requireDependency(
                "Назначение преподавателя на курс: проверка роли",
                "users-service",
                () -> userClient.isTeacher(teacherId)
        );
        if (response == null || !response.isTeacher()) {
            throw new IllegalArgumentException("this user is not a teacher");
        }
        Category category = course.getCategory();
        if (category == null) {
            return;
        }
        TeacherRequest teacher = requireDependency(
                "Назначение преподавателя на курс: проверка специализации",
                "users-service",
                () -> userClient.getTeacher(teacherId)
        );
        boolean hasRequiredCategory = teacher.specializations() != null
                && teacher.specializations().stream().anyMatch(s -> category.getId().equals(s.getId()));
        if (!hasRequiredCategory) {
            throw new IllegalArgumentException("teacher does not have required course category");
        }
    }

    private String resolveUserName(Long userId) {
        if (userId == null) {
            return "Course-service";
        }
        try {
            TeacherRequest user = referenceDataCacheService.getTeacher(userId);
            String fullName = (safe(user.name()) + " " + safe(user.surname())).trim();
            return fullName.isBlank() ? "Пользователь #" + userId : fullName;
        } catch (Exception e) {
            return "Пользователь #" + userId;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private <T> T requireDependency(String operation, String dependency, Supplier<T> call) {
        try {
            return call.get();
        } catch (RuntimeException e) {
            courseEventProducer.publishSystemAlert(new SystemAlertEvent(
                    "course-service",
                    operation,
                    dependency,
                    "HIGH",
                    "Операция не может быть безопасно выполнена без актуальных данных зависимого сервиса",
                    e.getClass().getSimpleName() + ": " + safe(e.getMessage())
            ));
            throw e;
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
