package com.teachsync.dto;

import com.teachsync.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {

    @Size(min = 2, max = 40, message = "invalid value for name")
    private String name;

    @Size(min = 2, max = 40, message = "invalid value for surname")
    private String surname;

    @Size(min = 2, max = 40, message = "invalid value for email")
    @Email
    private String email;

    private String profilePicture;

    private Role role;

    public UserUpdateDto(String name, String surname, String email, String profilePicture, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public UserUpdateDto() {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
