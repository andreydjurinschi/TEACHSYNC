package com.teachsync.interaction.requests.nested;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherBaseInfoRequest {
    private Long id;
    private String name;
    private String surname;
    private String fullName;
    private String email;
    private LocalDate registeredAt;
    private String role;
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

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
