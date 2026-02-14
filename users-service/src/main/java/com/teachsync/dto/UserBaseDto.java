package com.teachsync.dto;

import com.teachsync.domain.Role;

import java.time.LocalDate;

public class UserBaseDto {

    private Long id;
    private String fullName;
    private String email;
    private LocalDate registeredAt;
    private Role role;

    public UserBaseDto(Long id, String fullName, String email, LocalDate registeredAt, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.registeredAt = registeredAt;
        this.role = role;
    }

    public UserBaseDto() {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
