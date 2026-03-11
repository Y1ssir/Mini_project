package com.ems.chat.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="suggestions")
public class Suggestions {
    @Id
    private UUID id;
    @ManyToOne
    @JoinColumn(name="conversation_id")
    private ChatConversation conversation;
    @Column(name="incident_similaire_id")
    private UUID incidentSimilaireId;
    @Column(name="score_similarite")
    private float score_similarite;
    @Column(name="accepte")
    private boolean accepte;

    public Suggestions(){
        this.id=UUID.randomUUID();
        this.accepte=false;
    }

    public UUID getId(){return this.id;}
    public ChatConversation getConversation(){return this.conversation;}
    public UUID getIncidentSimilaireId(){return this.incidentSimilaireId;}
    public float getScore_similarite(){return this.score_similarite;}
    public boolean getAccepte(){return this.accepte;}

    public void setIncidentSimilaireId(UUID incidentSimilaireId){this.incidentSimilaireId=incidentSimilaireId;}
    public void setScore_similarite(float score_similarite){this.score_similarite=score_similarite;}
    public void setAccepte(boolean accepte){this.accepte=accepte;}
}
