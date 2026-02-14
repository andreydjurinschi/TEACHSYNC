package com.teachsync.courseservice.dto_s.groups;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class GroupCreateDto {
    @NotBlank(message = "group name is required")
    @Size(min = 4, max = 10, message = "group name needs to be between 4 and 10 chars")
    private String name;
    private LocalDate openDate;
    @Min(value = 12, message = "group min size needs to be 12")
    @Max(value = 35, message = "group max size needs to be 35")
    private Integer capacity;

    public GroupCreateDto(String name, LocalDate openDate, Integer capacity) {
        this.name = name;
        this.openDate = openDate;
        this.capacity = capacity;
    }

    public GroupCreateDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
