package com.teachsync.interaction.responses.feign;

import java.util.Set;

public record TeacherResponse(Long id, String name, String surname, String email, Set<SpecializationsBaseDto> specializations) {
}
