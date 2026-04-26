package com.app.incident.dto;

import com.app.incident.enums.IncidentPriority;
import com.app.incident.enums.IncidentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IncidentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private IncidentStatus status;
    private IncidentPriority priority;
    private String createdBy;
    private String assignedTo;
    private String category;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}
