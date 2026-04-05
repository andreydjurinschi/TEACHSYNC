package com.teachsync.dto;

import com.teachsync.domain.Role;

import java.time.LocalDate;

public class UserBaseDto {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate registeredAt;
    private Role role;

    public UserBaseDto(Long id, String name, String surname, String email, LocalDate registeredAt, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.registeredAt = registeredAt;
        this.role = role;
    }

    public UserBaseDto() {
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
