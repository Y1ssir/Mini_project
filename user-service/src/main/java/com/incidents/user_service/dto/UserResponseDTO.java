package com.incidents.user_service.dto;

import java.util.UUID;

public class UserResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private String department;
    private String position;

    // Constructeurs
    public UserResponseDTO() {}

    public UserResponseDTO(UUID id, String firstName, String lastName, String email,
                           String avatarUrl, String department, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.department = department;
        this.position = position;
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}