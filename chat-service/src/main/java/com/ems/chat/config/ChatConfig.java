package com.ems.chat.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public InMemoryChatMemoryRepository Repo(){
        return new InMemoryChatMemoryRepository();
    }
    @Bean
    public ChatMemory chatMemory() {
       return MessageWindowChatMemory.builder()
                .chatMemoryRepository(Repo())
                .maxMessages(10)
                .build();
    }

}
