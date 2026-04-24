package com.teachsync.interaction.responses.feign;

import com.teachsync.domain.Role;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String profilePicture;
    private LocalDate registeredAt;
    private Role role;
    private Set<SpecializationsBaseDto> specializations = new HashSet<>();

    public UserResponse(Long id, String name, String surname, String email, String password, String profilePicture, LocalDate registeredAt, Role role, Set<SpecializationsBaseDto> specializations) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.registeredAt = registeredAt;
        this.role = role;
        this.specializations = specializations;
    }

    public UserResponse() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Set<SpecializationsBaseDto> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<SpecializationsBaseDto> specializations) {
        this.specializations = specializations;
    }
}

