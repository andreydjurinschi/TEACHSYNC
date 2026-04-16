package com.teachsync.dto;

import com.teachsync.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UserCreateDto {

    @NotEmpty
    @Size(min = 2, max = 40, message = "invalid value for name")
    private String name;

    @NotEmpty
    @Size(min = 2, max = 40, message = "invalid value for surname")
    private String surname;

    @Size(min = 6, message = "password minimal range is 6 characters")
    @NotEmpty(message = "password is required")
    private String password;

    @NotEmpty
    @Size(min = 2, max = 40, message = "invalid value for email")
    @Email
    private String email;

    private LocalDate registeredAt;

    private String profilePicture;

    @NotNull(message = "role cannot be null or empty")
    private Role role;

    public UserCreateDto(String name, String surname, String password, String email, LocalDate registeredAt, String profilePicture, Role role) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.registeredAt = registeredAt;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserCreateDto() {
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
}
