package com.ems.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="chat_messages")
public class ChatMessages {
    public enum Speaker{
        USER,
        BOT
    }
    @Id
    private UUID id;
    @ManyToOne
    @JoinColumn(name="conversation_id",nullable = false)
    private ChatConversation conversation;
    @Column(name="expediteur")
    @Enumerated(EnumType.STRING)
    private Speaker expediteur;
    @Column(name="message",length=1000)
    private String message;
    @Column(name="timestamp")
    private LocalDateTime timestamp;

    public ChatMessages(){
        this.id=UUID.randomUUID();
        this.expediteur=Speaker.BOT;
        this.timestamp=LocalDateTime.now();
    }

    public UUID getId(){return id;}
    public ChatConversation getConversation(){return conversation;}
    public Speaker getExpediteur(){return expediteur;}
    public String getMessage(){return message;}
    public LocalDateTime getTimestamp(){return timestamp;}

    public void setExpediteur(Speaker expediteur){this.expediteur=expediteur;}
    public void setMessage(String message){this.message=message;}
    public void setConversation(ChatConversation conversation) {this.conversation = conversation;}
}
