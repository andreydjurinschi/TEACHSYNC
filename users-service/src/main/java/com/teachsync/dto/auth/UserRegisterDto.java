package com.teachsync.dto.auth;

import com.teachsync.domain.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserRegisterDto {
    @NotBlank
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    private String name;
    @NotBlank
    @Size(min = 2, max = 50, message = "surname must be between 2 and 50 characters")
    private String surname;
    @Size(min = 6, message = "password minimal range is 6 characters")
    @NotEmpty(message = "password is required")
    private String password;
    @NotBlank
    @Email
    @Size(min = 2, max = 100, message = "invalid email address")
    private String email;

    public UserRegisterDto(String name, String surname, String password, String email) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    public UserRegisterDto() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
