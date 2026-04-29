package com.teachsync.interaction.requests.nested;

import java.util.HashSet;
import java.util.Set;

public class TeacherBaseInfoRequest {
    private Long id;
    private String name;
    private String surname;
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

    public String displayName() {
        if (fullName != null && !fullName.isBlank()) {
            return fullName;
        }
        return ((name == null ? "" : name) + " " + (surname == null ? "" : surname)).trim();
    }
}
