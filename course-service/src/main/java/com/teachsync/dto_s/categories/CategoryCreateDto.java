package com.teachsync.dto_s.categories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryCreateDto {

    @NotBlank(message = "category name os required")
    @Size(min = 4, max = 35, message = "category name needs to be between 4 and 35 chars")
    private String name;

    public CategoryCreateDto(String name) {
        this.name = name;
    }

    public CategoryCreateDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
