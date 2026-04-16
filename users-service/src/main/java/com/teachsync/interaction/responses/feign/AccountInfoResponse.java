package com.teachsync.interaction.responses.feign;


import com.teachsync.domain.Role;

import java.time.LocalDate;

public class AccountInfoResponse {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private LocalDate registeredAt;
    private Role role;
    private String profilePicture;

    public AccountInfoResponse(Long id, String email, String name, String surname, LocalDate registeredAt, Role role, String profilePicture) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.registeredAt = registeredAt;
        this.role = role;
        this.profilePicture = profilePicture;
    }

    public AccountInfoResponse() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

