package org.cedacri.spring.scheduleservice.interation.feign.clients;

import org.cedacri.spring.scheduleservice.interation.feign.requests.TeacherBaseInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080/internal/teachsync/users")
public interface TeacherClient {
    @GetMapping("/{id}")
    TeacherBaseInfoRequest requestForUserFromUserService(@PathVariable("id") Long userId);
}
