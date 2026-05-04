package com.teachsync.mapper;

import com.teachsync.domain.Role;
import com.teachsync.domain.User;
import com.teachsync.dto.UserCreateDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void mapToUserKeepsInitialPasswordForHashing() {
        UserCreateDto dto = new UserCreateDto(
                "Test",
                "User",
                "secret123",
                "test.user@example.com",
                LocalDate.now(),
                null,
                Role.TEACHER
        );

        User user = UserMapper.mapToUser(dto);

        assertEquals("secret123", user.getPassword());
    }
}
