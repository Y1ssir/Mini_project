package com.ems.chat.repository;

import com.ems.chat.entity.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessagesRepo extends JpaRepository<ChatMessages, UUID> {
    List<ChatMessages> findByConversationId(UUID conversationID);
    @Query("select m from ChatMessages m where m.timestamp > :myTimeStamp and m.conversation.id = :myConversationId")
    List<ChatMessages> filterByTimeStamp(@Param("myTimeStamp") LocalDateTime timeStamp, @Param("myConversationId") UUID myConversation);
}
