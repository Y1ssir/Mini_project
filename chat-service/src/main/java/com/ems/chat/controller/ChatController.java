package com.ems.chat.controller;

import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("api/chat")
public class ChatController {
    @Value("classpath:/prompt/ai_rules.st")
    private Resource ai_rules;
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @GetMapping("/ask")
    public Flux<String> askAI(@RequestParam(value = "message") String message) {
        PromptTemplate template = new PromptTemplate(ai_rules);
        Map<String, Object> map = Map.of(
                "company_name", "ENACTUS",
                "current_date", LocalDate.now().toString()
        );
        Message system_rules = template.createMessage(map);
        return this.chatClient.prompt()
                .system(system_rules.getText())
                .user(message)
                .stream()
                .content();
    }
}

