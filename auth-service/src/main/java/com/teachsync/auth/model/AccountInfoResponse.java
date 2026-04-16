package com.teachsync.auth.model;

import com.teachsync.interaction.feign.requests.Role;

import java.time.LocalDate;

public class AccountInfoResponse {
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate registeredAt;
    private Role role;
    private String profilePicture;

    public AccountInfoResponse(String email, String firstName, String lastName, LocalDate registeredAt, Role role, String profilePicture) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
