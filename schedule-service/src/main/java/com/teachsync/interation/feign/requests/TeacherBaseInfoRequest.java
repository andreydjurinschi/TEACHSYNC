package com.teachsync.interation.feign.requests;

import com.teachsync.interation.feign.Role;

import java.time.LocalDate;

public class TeacherBaseInfoRequest {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate registeredAt;
    private Role role;

    public TeacherBaseInfoRequest(Long id, String name, String surname, String email, LocalDate registeredAt, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.registeredAt = registeredAt;
        this.role = role;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
