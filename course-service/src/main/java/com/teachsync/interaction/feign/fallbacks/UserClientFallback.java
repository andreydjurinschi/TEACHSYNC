package com.teachsync.interaction.feign.fallbacks;

import com.teachsync.exceptions.ServiceUnavailableException;
import com.teachsync.interaction.feign.clients.UserClient;
import com.teachsync.interaction.feign.requests.TeacherCheckRequest;
import com.teachsync.interaction.feign.requests.TeacherRequest;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public TeacherCheckRequest isTeacher(Long id) {
        throw new ServiceUnavailableException("user service is not available rn.\n Method: verifying if user is teacher");
    }

    @Override
    public TeacherRequest getTeacher(Long id) {
        throw new ServiceUnavailableException("user service is not available rn.\n Method: get teacher");
    }
}
