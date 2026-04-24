package com.teachsync.mapper;

import com.teachsync.domain.Category;
import com.teachsync.interaction.responses.feign.SpecializationsBaseDto;

public class SpecializationMapper {
    public static SpecializationsBaseDto mapToBaseDto(Category category){
        return new  SpecializationsBaseDto(
                category.getId(), category.getName()
        );
    }
}
