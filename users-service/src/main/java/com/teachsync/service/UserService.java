package com.teachsync.service;

import com.teachsync.domain.User;
import com.teachsync.interaction.clients.CourseClient;
import com.teachsync.interaction.requests.CourseBaseDto;
import com.teachsync.dto.feign.UserWithCoursesDto;
import com.teachsync.interaction.responses.feign.TeacherResponseForCourseService;
import com.teachsync.interaction.responses.feign.UserResponse;
import com.teachsync.mapper.UserMapper;
import com.teachsync.repository.UserRepository;
import com.teachsync.dto.UserBaseDto;
import com.teachsync.dto.UserCreateDto;
import com.teachsync.dto.UserUpdateDto;
import com.teachsync.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    private final UserRepository repository;
    private final CourseClient courseClient;

    @Autowired
    public UserService(UserRepository repository, CourseClient courseClient) {
        this.repository = repository;
        this.courseClient = courseClient;
    }

    public List<UserBaseDto> findAll(){
        List<UserBaseDto> dtos = new ArrayList<>();
        for(var user : repository.findAll()){
            dtos.add(UserMapper.mapToBaseDto(user));
        }
        return dtos;
    }

    public UserResponse getByUserEmail(String email){
        User user = repository.findUserByEmail(email);
        if(user == null){
            return null;
        }
        return new UserResponse(
          user.getId(), user.getName(), user.getSurname(), user.getEmail(), user.getPassword(), user.getRegisteredAt(), user.getRole()
        );
    }

    public UserBaseDto findById(Long id){
        if(id == null){
            return null;
        }
        User user = getUser(id);
        return UserMapper.mapToBaseDto(user);
    }

    @Transactional
    public void createUser(UserCreateDto dto){
        User user = UserMapper.mapToUser(dto);
        user.setRegisteredAt(LocalDate.now());
        user.setPassword(PasswordUtils.hash(user.getPassword()));
        repository.save(user);
    }

    @Transactional
    public void updateUser(Long id, UserUpdateDto dto) {
        User user = getUser(id);
        if (StringUtils.hasText(dto.getName())) user.setName(dto.getName());
        if (StringUtils.hasText(dto.getSurname())) user.setSurname(dto.getSurname());
        if (StringUtils.hasText(dto.getEmail())) user.setEmail(dto.getEmail());
    }


    @Transactional
    public void deleteUser(Long id){
        User user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        repository.delete(user);
    }

    // feign

    public UserWithCoursesDto getUserWithCourses(Long userId) {
        User user = getUser(userId);
        UserWithCoursesDto dto = new UserWithCoursesDto();
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        List<CourseBaseDto> courses;
        /*try{*/
            courses = courseClient.requestForCourseInfo(userId);
            dto.setCourseNames(courses);
            dto.setAvailable(true);
            return dto;
        /*}catch (Exception e){
            dto.setCourseNames(Collections.emptyList());
            dto.setAvailable(false);
            return dto;
        }*/
    }

    public TeacherResponseForCourseService getTeacherForCourseService(Long id){
        User user = getUser(id);
        return new TeacherResponseForCourseService(
                user.getId(), user.getName(), user.getSurname(), user.getEmail()
        );
    }

    private User getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

}
