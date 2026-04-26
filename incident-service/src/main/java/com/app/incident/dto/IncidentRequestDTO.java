package com.app.incident.dto;

import com.app.incident.enums.IncidentPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidentRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "La priorité est obligatoire")
    private IncidentPriority priority;

    private String category;
    private String attachmentUrl; // fourni par MinIO (Member 2)
}
