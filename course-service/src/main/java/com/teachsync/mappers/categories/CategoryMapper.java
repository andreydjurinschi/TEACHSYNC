package com.teachsync.mappers.categories;

import com.teachsync.domain.Category;
import com.teachsync.dto_s.categories.CategoryBaseDto;
import com.teachsync.dto_s.categories.CategoryCreateDto;

public class CategoryMapper {

    public static CategoryBaseDto mapToDto(Category category) {
        return new CategoryBaseDto(category.getId(), category.getName());
    }

    public static Category mapToEntity(CategoryCreateDto createDto) {
        return new Category(createDto.getName());
    }
}
