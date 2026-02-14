package com.teachsync.controller.internal;

import com.teachsync.domain.Role;
import com.teachsync.interaction.responses.feign.TeacherBaseInfoForScheduleServiceResponse;
import com.teachsync.interaction.responses.feign.TeacherCheckResponse;
import com.teachsync.dto.UserBaseDto;
import com.teachsync.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/teachsync/users")
public class UserInternalController {

    private final UserService userService;

    public UserInternalController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/teacher")
    public TeacherCheckResponse isTeacher(@PathVariable Long id){
        UserBaseDto user = userService.findById(id);
        return new TeacherCheckResponse(
                user.getRole() == Role.TEACHER
        );
    }

    @GetMapping("/{id}")
    public TeacherBaseInfoForScheduleServiceResponse baseInfoForScheduleServiceResponse(@PathVariable Long id){
        UserBaseDto user = userService.findById(id);
        return new TeacherBaseInfoForScheduleServiceResponse(
                user.getId(), user.getFullName(), user.getEmail()
        );
    }
}
