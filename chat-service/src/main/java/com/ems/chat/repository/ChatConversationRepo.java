package com.ems.chat.repository;

import com.ems.chat.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ChatConversationRepo extends JpaRepository<ChatConversation, UUID> {
}
