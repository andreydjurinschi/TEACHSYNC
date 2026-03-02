package com.teachsync.interaction.feign.clients;

import com.teachsync.interaction.feign.fallbacks.UserClientFallback;
import com.teachsync.interaction.feign.requests.TeacherCheckRequest;
import com.teachsync.interaction.feign.requests.TeacherRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//todo: service discovery

/**
 * feign клиент
 */
@FeignClient(
        name = "user-service", // сервис с пользователями
        url = "http://localhost:8080/internal/users", // адрес сервиса
        fallback = UserClientFallback.class // класс fallback, вызываемый при ошибке
)
public interface UserClient {

    // метод проверки если пользователь - учитель
    @GetMapping("/{id}/teacher")
    TeacherCheckRequest isTeacher(@PathVariable Long id);

    // метод для получения самого учителя
    @GetMapping("/course_service/{id}")
    TeacherRequest getTeacher(@PathVariable Long id);
}
