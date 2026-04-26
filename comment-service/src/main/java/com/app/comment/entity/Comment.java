package com.app.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments",
       indexes = @Index(name = "idx_incident_id", columnList = "incident_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence à l'incident (clé étrangère logique — pas de FK réelle entre services)
    @Column(name = "incident_id", nullable = false)
    private Long incidentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // ID Keycloak de l'auteur
    @Column(name = "author_id", nullable = false)
    private String authorId;

    // Nom affiché (dénormalisé pour éviter un appel réseau à chaque lecture)
    @Column(name = "author_name")
    private String authorName;

    // ADMIN = note technicien, CLIENT = message utilisateur, SYSTEM = action automatique
    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", nullable = false)
    private CommentType commentType;

    // URL pièce jointe MinIO (optionnel)
    @Column(name = "attachment_url")
    private String attachmentUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CommentType {
        CLIENT,   // message du client
        ADMIN,    // note du technicien
        SYSTEM    // action automatique (ex: "Statut changé à RESOLVED")
    }
}
