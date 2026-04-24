package com.teachsync.interaction.responses.feign;

import java.util.HashSet;
import java.util.Set;

public class TeacherBaseInfoForScheduleServiceResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Set<SpecializationsBaseDto> specializations = new HashSet<SpecializationsBaseDto>();

    public TeacherBaseInfoForScheduleServiceResponse(Long id, String name, String surname, String email, Set<SpecializationsBaseDto> specializations) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.specializations = specializations;
    }

    public TeacherBaseInfoForScheduleServiceResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Set<SpecializationsBaseDto> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<SpecializationsBaseDto> specializations) {
        this.specializations = specializations;
    }
}
