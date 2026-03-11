package com.ems.chat.services.message;

import com.ems.chat.dto.message.ChatRequestDTO;
import com.ems.chat.dto.message.ChatResponseDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.entity.ChatMessages;
import com.ems.chat.mapper.message.ChatMapper;
import com.ems.chat.repository.ChatConversationRepo;
import com.ems.chat.repository.ChatMessagesRepo;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.client.ChatClient;
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
    private final ChatClient chatClient;

    public ChatServicesImpl(
            ChatMessagesRepo chatRepo,
            ChatConversationRepo conversationRepo,
            ChatMapper chatMapper,
            ChatClient.Builder chatClientBuilder
    ) {
        this.chatRepo = chatRepo;
        this.conversationRepo = conversationRepo;
        this.chatMapper = chatMapper;
        this.chatClient = chatClientBuilder.build();
    }
    @Override
    public Flux<ChatResponseDTO> ChatAnswer(ChatRequestDTO request) {
        String sender = "BOT";
        String timestamp = LocalDateTime.now().toString();
        AtomicReference<String> fullResponse = new AtomicReference<>("");
        Mono<ChatConversation> conversationMono = Mono.<ChatConversation>fromCallable(() ->
                conversationRepo.findById(request.conversationId())
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
        Flux<ChatResponseDTO> stream = chatClient.prompt(request.message())
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
    public List<ChatResponseDTO> GetHistory(UUID conversation_id) {
        List<ChatMessages> history = chatRepo.findByConversationId(conversation_id);
        return chatMapper.toDTOList(history);
    }
    @Override
    @Transactional
    public Mono<Void> ChangeUpdate(ChatRequestDTO request) {
        return Mono.fromCallable(() -> {
                    ChatMessages messageEntity = chatRepo.findById(request.messageId())
                            .orElseThrow(() -> new RuntimeException("Message introuvable"));
                    List<ChatMessages> messageToDelete = chatRepo.filterByTimeStamp(
                            messageEntity.getTimestamp(),
                            messageEntity.getConversation().getId()
                    );
                    chatRepo.deleteAll(messageToDelete);
                    messageEntity.setMessage(request.message());
                    chatRepo.save(messageEntity);
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
