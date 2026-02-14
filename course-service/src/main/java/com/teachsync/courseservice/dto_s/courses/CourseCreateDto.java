package com.teachsync.courseservice.dto_s.courses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CourseCreateDto {

    @NotEmpty
    @Size(min = 5, max = 50, message = "name length needs to be between 5-50 characters")
    private String name;

    @NotEmpty
    @Size(min = 15, max = 200, message = "description length needs to be between 15-200 characters")
    private String description;

    private String photoUrl;

    public CourseCreateDto(String name, String description, String photoUrl) {
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
