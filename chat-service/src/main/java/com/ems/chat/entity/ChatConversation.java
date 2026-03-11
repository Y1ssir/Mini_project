package com.ems.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="chat_conversations")
public class ChatConversation {
    public enum Statutconversation {
        EN_COURS,
        RESOLU,
        INCIDENT_CREE
    }
    @Id
    private UUID id;
    @Column(name="utilisateur_id")
    private UUID utilisateurId;
    @Column(name="date_creation")
    private LocalDateTime dateCreation;
    @Column(name="statut")
    @Enumerated(EnumType.STRING)
    private Statutconversation statut;
    @Column(name="probleme_resume",length=1000)
    private String problemeResume;
    @OneToMany(mappedBy = "conversation")
    private List<ChatMessages> messages;
    @OneToMany(mappedBy = "conversation")
    private List<Suggestions> suggestions;

    public ChatConversation() {
        this.id = UUID.randomUUID();
        this.dateCreation = LocalDateTime.now();
        this.statut = Statutconversation.EN_COURS;
    }
    public UUID getId() { return id; }
    public UUID getUtilisateurId() { return utilisateurId; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Statutconversation getStatut() { return statut; }
    public String getProblemeResume() { return problemeResume; }
    public List<ChatMessages> getMessages() { return messages; }
    public List<Suggestions> getSuggestions() { return suggestions; }

    public void setUtilisateurId(UUID utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setStatut(Statutconversation statut) { this.statut = statut; }
    public void setProblemeResume(String problemeResume) { this.problemeResume = problemeResume; }
}
