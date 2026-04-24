package com.teachsync.interaction.requests.nested;

import java.util.HashSet;
import java.util.Set;

public class TeacherBaseInfoRequest {
    private Long id;
    private String fullName;
    private String email;
    private Set<SpecializationsBaseDto> specializations = new HashSet<>();

    public TeacherBaseInfoRequest(Long id, String fullName, String email, Set<SpecializationsBaseDto> specializations) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.specializations = specializations;
    }

    public TeacherBaseInfoRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<SpecializationsBaseDto> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<SpecializationsBaseDto> specializations) {
        this.specializations = specializations;
    }
}
