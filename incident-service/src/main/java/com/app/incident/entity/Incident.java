package com.app.incident.entity;

import com.app.incident.enums.IncidentPriority;
import com.app.incident.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentPriority priority;

    // ID Keycloak de l'utilisateur qui a créé l'incident
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    // ID Keycloak du technicien assigné (peut être null)
    @Column(name = "assigned_to")
    private String assignedTo;

    // Catégorie (réseau, matériel, logiciel, etc.)
    @Column
    private String category;

    // URL MinIO de la pièce jointe (géré par Member 2)
    @Column(name = "attachment_url")
    private String attachmentUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Date de résolution effective
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
