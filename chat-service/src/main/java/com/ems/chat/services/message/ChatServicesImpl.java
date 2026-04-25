package com.ems.chat.services.message;

import com.ems.chat.dto.message.ChatRequestDTO;
import com.ems.chat.dto.message.ChatResponseDTO;
import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.entity.ChatMessages;
import com.ems.chat.mapper.message.ChatMapper;
import com.ems.chat.repository.ChatConversationRepo;
import com.ems.chat.repository.ChatMessagesRepo;
import com.ems.chat.services.rag.RagService;
import com.ems.chat.services.suggestion.SuggestionsService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ChatServicesImpl implements ChatServices {
    private final ChatMessagesRepo chatRepo;
    private final ChatConversationRepo conversationRepo;
    private final ChatMapper chatMapper;
    private final org.springframework.ai.chat.client.ChatClient chatClient; // ✅
    private final RagService ragService;
    private final SuggestionsService suggestionsService;

    public ChatServicesImpl(
            ChatMessagesRepo chatRepo,
            ChatConversationRepo conversationRepo,
            ChatMapper chatMapper,
            org.springframework.ai.chat.client.ChatClient.Builder chatClientBuilder,
            RagService ragService,
            SuggestionsService suggestionsService
    ) {
        this.chatRepo = chatRepo;
        this.conversationRepo = conversationRepo;
        this.chatMapper = chatMapper;
        this.chatClient = chatClientBuilder.build();
        this.ragService = ragService;
        this.suggestionsService = suggestionsService;
    }

    @Override
    public Flux<ChatResponseDTO> ChatAnswer(UUID conversationId, ChatRequestDTO request) {
        String sender = "BOT";
        String timestamp = LocalDateTime.now().toString();
        AtomicReference<String> fullResponse = new AtomicReference<>("");

        List<IncidentSimilaireDTO> incidents = ragService.findSimilaires(request.message());
        String incidentsFormatted = ragService.formatForPrompt(incidents);
        suggestionsService.saveSuggestions(conversationId, incidents);

        Mono<ChatConversation> conversationMono = Mono.<ChatConversation>fromCallable(() ->
                conversationRepo.findById(conversationId)
                        .orElseThrow(() -> new RuntimeException("Conversation introuvable"))
        ).subscribeOn(Schedulers.boundedElastic()).cache();

        Mono<Void> saveUser = conversationMono.flatMap(conversation ->
                Mono.fromRunnable(() -> {
                    ChatMessages userEntity = chatMapper.toEntity(request);
                    userEntity.setConversation(conversation);
                    userEntity.setExpediteur(ChatMessages.Speaker.USER);
                    chatRepo.save(userEntity);
                }).subscribeOn(Schedulers.boundedElastic())
        ).then();

        Flux<ChatResponseDTO> stream = chatClient.prompt()
                .system("""
                    Tu es un assistant IT.
                    Voici des incidents similaires résolus :
                    %s
                    Propose ces solutions à l'utilisateur.
                    Si aucune ne fonctionne, propose de créer un incident.
                """.formatted(incidentsFormatted))
                .user(request.message())
                .stream()
                .content()
                .filter(chunk -> chunk != null && !chunk.isEmpty())
                .index()
                .map(indexedChunk -> {
                    String text = indexedChunk.getT2();
                    fullResponse.updateAndGet(current -> current + text);
                    ChatResponseDTO.FluxStatut status = (indexedChunk.getT1() == 0)
                            ? ChatResponseDTO.FluxStatut.START
                            : ChatResponseDTO.FluxStatut.STREAM;
                    return new ChatResponseDTO(status, text, sender, timestamp);
                });

        Flux<ChatResponseDTO> endSignal = Flux.defer(() ->
                conversationMono.flatMap(conversation ->
                                Mono.fromRunnable(() -> {
                                    ChatMessages botEntity = new ChatMessages();
                                    botEntity.setConversation(conversation);
                                    botEntity.setMessage(fullResponse.get());
                                    botEntity.setExpediteur(ChatMessages.Speaker.BOT);
                                    chatRepo.save(botEntity);
                                }).subscribeOn(Schedulers.boundedElastic())
                        ).thenReturn(new ChatResponseDTO(
                                ChatResponseDTO.FluxStatut.END, "", sender, timestamp))
                        .flux()
        );

        return saveUser
                .thenMany(stream.concatWith(endSignal))
                .onErrorResume(e -> Flux.just(new ChatResponseDTO(
                        ChatResponseDTO.FluxStatut.END, "Erreur: " + e.getMessage(), sender, timestamp)
                ));
    }

    @Override
    public List<ChatResponseDTO> GetHistory(UUID conversationId) {
        List<ChatMessages> history = chatRepo.findByConversationId(conversationId);
        return chatMapper.toDTOList(history);
    }

    @Override
    @Transactional
    public Mono<Void> ChangeAnswer(UUID messageId, ChatRequestDTO request) {
        return Mono.fromRunnable(() -> {
            ChatMessages messageEntity = chatRepo.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message introuvable : " + messageId));
            List<ChatMessages> messageToDelete = chatRepo.filterByTimeStamp(
                    messageEntity.getTimestamp(),
                    messageEntity.getConversation().getId()
            );
            if (!messageToDelete.isEmpty()) {
                chatRepo.deleteAll(messageToDelete);
            }
            messageEntity.setMessage(request.message());
            chatRepo.save(messageEntity);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}