package com.teachsync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AccountUpdateDto {
    @NotEmpty
    @Size(min = 2, max = 40, message = "invalid value for name")
    private String name;

    @NotEmpty
    @Size(min = 2, max = 40, message = "invalid value for surname")
    private String surname;

    @Size(min = 6, message = "password minimal range is 6 characters")
    private String password;

    @NotEmpty
    @Size(min = 2, max = 40, message = "invalid value for email")
    @Email
    private String email;

    private String profilePicture;

    public AccountUpdateDto(String name, String surname, String password, String email, String profilePicture) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountUpdateDto() {
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
