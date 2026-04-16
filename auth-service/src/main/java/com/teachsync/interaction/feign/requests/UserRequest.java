package com.teachsync.interaction.feign.requests;

import java.time.LocalDate;

public class UserRequest {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private LocalDate registeredAt;
    private String profilePicture;
    private Role role;

    public UserRequest(Long id, String name, String surname, String email, String password, LocalDate registeredAt, String profilePicture, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.registeredAt = registeredAt;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public UserRequest() {
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
    public void setProfilePicture(String profilePicture) {}
}

