package com.teachsync.service;

import com.teachsync.domain.Category;
import com.teachsync.domain.Role;
import com.teachsync.domain.User;
import com.teachsync.dto.AccountUpdateDto;
import com.teachsync.interaction.KAFKA.producer.UserEventProducer;
import com.teachsync.interaction.clients.CourseClient;
import com.teachsync.interaction.requests.CourseBaseDto;
import com.teachsync.dto.feign.UserWithCoursesDto;
import com.teachsync.interaction.responses.feign.SpecializationsBaseDto;
import com.teachsync.interaction.responses.feign.TeacherResponse;
import com.teachsync.interaction.responses.feign.UserResponse;
import com.teachsync.mapper.SpecializationMapper;
import com.teachsync.mapper.UserMapper;
import com.teachsync.repository.SpecializationsRepository;
import com.teachsync.repository.UserRepository;
import com.teachsync.dto.UserBaseDto;
import com.teachsync.dto.UserCreateDto;
import com.teachsync.dto.UserUpdateDto;
import com.teachsync.dto.statistics.UserStatisticsDto;
import com.teachsync.teachsyncevents.users.UserCreatedEvent;
import com.teachsync.teachsyncevents.users.UserDeletedEvent;
import com.teachsync.teachsyncevents.users.UserRoleChangedEvent;
import com.teachsync.teachsyncevents.users.UserSpecializationAddedEvent;
import com.teachsync.teachsyncevents.users.UserSpecializationRemovedEvent;
import com.teachsync.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repository;
    private final CourseClient courseClient;
    private final SpecializationsRepository specializationsRepository;
    private final UserEventProducer userEventProducer;

    public UserService(UserRepository repository, CourseClient courseClient, SpecializationsRepository specializationsRepository, UserEventProducer userEventProducer) {
        this.repository = repository;
        this.courseClient = courseClient;
        this.specializationsRepository = specializationsRepository;
        this.userEventProducer = userEventProducer;
    }

    public List<UserBaseDto> findAll() {
        List<UserBaseDto> dtos = new ArrayList<>();
        for (var user : repository.findAll()) {
            dtos.add(UserMapper.mapToBaseDto(user));
        }
        return dtos;
    }

    public UserResponse getByUserEmail(String email) {
        User user = repository.findUserByEmail(email);
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPassword(),
                user.getProfilePicture(),
                user.getRegisteredAt(),
                user.getRole(),
                user.getSpecializations().stream().map(SpecializationMapper::mapToBaseDto).collect(Collectors.toSet())
        );
    }


    public UserBaseDto findById(Long id) {
        if (id == null) {
            return null;
        }
        User user = getUser(id);
        return UserMapper.mapToBaseDto(user);
    }

    @Transactional
    public void createUser(UserCreateDto dto) {
        User user = UserMapper.mapToUser(dto);
        user.setProfilePicture(normalizeProfilePicture(dto.getProfilePicture()));
        user.setRegisteredAt(LocalDate.now());
        user.setPassword(PasswordUtils.hash(user.getPassword()));
        repository.save(user);
        userEventProducer.publishUserCreated(new UserCreatedEvent(
                user.getId(), user.getRole().toString(), user.getEmail(),
                user.getName(), user.getSurname()
        ));
    }

    @Transactional
    public void updateUser(Long id, UserUpdateDto dto) {
        User user = getUser(id);
        if (StringUtils.hasText(dto.getName())) user.setName(dto.getName());
        if (StringUtils.hasText(dto.getSurname())) user.setSurname(dto.getSurname());
        if (StringUtils.hasText(dto.getEmail())) user.setEmail(dto.getEmail());
        if (dto.getProfilePicture() != null) user.setProfilePicture(normalizeProfilePicture(dto.getProfilePicture()));
        if (dto.getRole() != null) {
            Role role = user.getRole();
            user.setRole(dto.getRole());
            userEventProducer.publishUserRoleChanged(new UserRoleChangedEvent(
                    user.getId(), role.name(), user.getRole().name()
            ));
        }
    }

    @Transactional
    public void editUserAccount(Long id, AccountUpdateDto dto) {
        User user = getUser(id);
        if (StringUtils.hasText(dto.getName())) user.setName(dto.getName());
        if (StringUtils.hasText(dto.getSurname())) user.setSurname(dto.getSurname());
        if (StringUtils.hasText(dto.getEmail())) user.setEmail(dto.getEmail());
        if (dto.getProfilePicture() != null) user.setProfilePicture(normalizeProfilePicture(dto.getProfilePicture()));
        if (StringUtils.hasText(dto.getPassword())) {
            String hashedPassword = PasswordUtils.hash(dto.getPassword());
            user.setPassword(hashedPassword);
        }
        ;
    }

    public List<UserBaseDto> findAllByRole(Role role) {
        List<User> users = repository.findAllByRole(role);
        List<UserBaseDto> result = new ArrayList<>();
        for (User user : users) {

            result.add(UserMapper.mapToBaseDto(
                    user
            ));
        }
        return result;
    }


    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        User temp = user;
        repository.delete(user);
        userEventProducer.publishUserDeleted(new UserDeletedEvent(
                temp.getId(), temp.getEmail(), temp.getName(), temp.getSurname(), temp.getRole().name()
        ));
    }

    // feign
    public UserWithCoursesDto getUserWithCourses(Long userId) {
        User user = getUser(userId);
        UserWithCoursesDto dto = new UserWithCoursesDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        List<CourseBaseDto> courses;

        courses = courseClient.requestForCourseInfo(userId);
        dto.setCourseNames(courses);
        dto.setAvailable(true);
        return dto;
    }

    public TeacherResponse getTeacherForCourseService(Long id) {
        User user = getUser(id);
        return new TeacherResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getSpecializations().stream().map(SpecializationMapper::mapToBaseDto).collect(Collectors.toSet())
        );
    }

    @Transactional
    public void addSpecializationForTeacher(Long teacherId, Long categoryId) {
        User user = repository.findById(teacherId).orElseThrow(() -> new NoSuchElementException("Teacher not found"));
        Category category = specializationsRepository.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
        if (user.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("Specialization can be assigned only to teacher");
        }

        specializationsRepository.addSpecializationForUser(user.getId(), category.getId());
        userEventProducer.publishUserSpecializationAdded(new UserSpecializationAddedEvent(
                user.getId(), category.getName()
        ));

    }

    @Transactional
    public void removeSpecializationForTeacher(Long teacherId, Long categoryId) {
        User user = repository.findById(teacherId).orElseThrow(() -> new NoSuchElementException("Teacher not found"));
        Category category = specializationsRepository.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
        if (user.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("Specialization can be removed only from teacher");
        }

        specializationsRepository.removeSpecializationForUser(user.getId(), category.getId());

        userEventProducer.publishUserSpecializationRemoved(new UserSpecializationRemovedEvent(
                user.getId(), category.getName()
        ));

    }

    public Set<SpecializationsBaseDto> getSpecializations(Long id) {
        User user = getUser(id);
        return user.getSpecializations().stream().map(SpecializationMapper::mapToBaseDto).collect(Collectors.toSet());
    }

    private User getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public List<UserBaseDto> findAllByIds(List<Long> ids) {
        return repository.findAllById(ids)
                .stream().map(UserMapper::mapToBaseDto).toList();
    }

    public UserStatisticsDto getStatistics() {
        return new UserStatisticsDto(
                repository.count(),
                repository.countByRole(Role.ADMIN),
                repository.countByRole(Role.MANAGER),
                repository.countByRole(Role.TEACHER)
        );
    }

    private String normalizeProfilePicture(String profilePicture) {
        if (!StringUtils.hasText(profilePicture)) {
            return null;
        }
        return profilePicture.trim();
    }
}
