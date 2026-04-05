package com.teachsync.interation.feign.requests;

import com.teachsync.interation.feign.Role;

import java.time.LocalDate;

public class TeacherBaseInfoRequest {
    private Long id;
    private String fullName;
    private String email;
    private LocalDate registeredAt;
    private Role role;

    public TeacherBaseInfoRequest(Long id, String fullName, String email, LocalDate registeredAt, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.registeredAt = registeredAt;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
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
}
