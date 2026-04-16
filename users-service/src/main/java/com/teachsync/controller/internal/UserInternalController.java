package com.teachsync.controller.internal;

import com.teachsync.domain.Role;
import com.teachsync.interaction.responses.feign.AccountInfoResponse;
import com.teachsync.interaction.responses.feign.TeacherBaseInfoForScheduleServiceResponse;
import com.teachsync.interaction.responses.feign.TeacherCheckResponse;
import com.teachsync.dto.UserBaseDto;
import com.teachsync.interaction.responses.feign.TeacherResponse;
import com.teachsync.interaction.responses.feign.UserResponse;
import com.teachsync.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * контроллер, отправляющий ответ другому сервису
 */
@RestController
@RequestMapping("/internal/users")
public class UserInternalController {

    private final UserService userService;

    public UserInternalController(UserService userService) {
        this.userService = userService;
    }

    // проверка если пользователь - учитель
    @GetMapping("/{id}/teacher")
    public TeacherCheckResponse isTeacher(@PathVariable Long id){
        UserBaseDto user = userService.findById(id);
        return new TeacherCheckResponse(
                user.getRole() == Role.TEACHER
        );
    }
    // получение учителя
    @GetMapping("/course_service/{id}")
    public TeacherResponse teacherResponseForCourseService(@PathVariable Long id){
        return userService.getTeacherForCourseService(id);
    }

    @GetMapping("/{id}")
    public TeacherBaseInfoForScheduleServiceResponse baseInfoForScheduleServiceResponse(@PathVariable Long id){
        UserBaseDto user = userService.findById(id);

        return new TeacherBaseInfoForScheduleServiceResponse(
                user.getId(), user.getName() + " " + user.getSurname(), user.getEmail()
        );
    }

    @GetMapping("/all/by-role")
    public List<UserBaseDto> allUsers(@RequestParam Role role){
        return userService.findAllByRole(role);
    }

    @GetMapping("/by-email/{email}")
    public UserResponse getByUserEmail(@PathVariable String email){
        return userService.getByUserEmail(email);
    }

    @GetMapping("/account/info")
    public AccountInfoResponse getUserInfo(@RequestParam String email){
        UserResponse response =  userService.getByUserEmail(email);

        return populateAccountInfoResponse(response);
    }

    private static AccountInfoResponse populateAccountInfoResponse(UserResponse response) {
        AccountInfoResponse accountInfoResponse = new AccountInfoResponse();
        accountInfoResponse.setEmail(response.getEmail());
        accountInfoResponse.setFirstName(response.getName());
        accountInfoResponse.setLastName(response.getSurname());
        accountInfoResponse.setRole(response.getRole());
        accountInfoResponse.setProfilePicture(response.getProfilePicture());
        accountInfoResponse.setRegisteredAt(response.getRegisteredAt());
        return accountInfoResponse;
    }
}
