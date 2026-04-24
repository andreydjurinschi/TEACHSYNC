package com.teachsync.interaction.feign.requests;

import com.teachsync.dto_s.feign.SpecializationsBaseDto;

import java.util.Set;

public record TeacherRequest(Long id, String name, String surname, String email, Set<SpecializationsBaseDto> specializations) {
}
