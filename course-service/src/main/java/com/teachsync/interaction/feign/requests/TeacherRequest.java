package com.teachsync.interaction.feign.requests;

public record TeacherRequest(Long id, String name, String surname, String email) {
}
