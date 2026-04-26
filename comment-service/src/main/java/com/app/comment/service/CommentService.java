package com.app.comment.service;

import com.app.comment.dto.CommentRequestDTO;
import com.app.comment.dto.CommentResponseDTO;
import com.app.comment.entity.Comment;
import com.app.comment.entity.Comment.CommentType;
import com.app.comment.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    // ─── CRÉATION ───────────────────────────────────────────────────────────────

    public CommentResponseDTO addComment(CommentRequestDTO dto, String authorId,
                                         String authorName, boolean isAdmin) {
        CommentType type = dto.getCommentType() != null
                ? dto.getCommentType()
                : (isAdmin ? CommentType.ADMIN : CommentType.CLIENT);

        Comment comment = Comment.builder()
                .incidentId(dto.getIncidentId())
                .content(dto.getContent())
                .authorId(authorId)
                .authorName(authorName)
                .commentType(type)
                .attachmentUrl(dto.getAttachmentUrl())
                .build();

        return toDTO(commentRepository.save(comment));
    }

    /**
     * Commentaire système automatique — appelé par d'autres services
     * (ex: lors du changement de statut d'un incident)
     */
    public CommentResponseDTO addSystemComment(Long incidentId, String message) {
        Comment comment = Comment.builder()
                .incidentId(incidentId)
                .content(message)
                .authorId("SYSTEM")
                .authorName("Système")
                .commentType(CommentType.SYSTEM)
                .build();

        return toDTO(commentRepository.save(comment));
    }

    // ─── LECTURE ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getByIncident(Long incidentId, Pageable pageable) {
        return commentRepository
                .findByIncidentIdOrderByCreatedAtAsc(incidentId, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public long countByIncident(Long incidentId) {
        return commentRepository.countByIncidentId(incidentId);
    }

    // ─── MODIFICATION ────────────────────────────────────────────────────────────

    public CommentResponseDTO updateComment(Long id, String newContent, String requesterId) {
        Comment comment = findOrThrow(id);

        // Seul l'auteur peut modifier son commentaire
        if (!comment.getAuthorId().equals(requesterId)) {
            throw new IllegalStateException("Vous ne pouvez modifier que vos propres commentaires");
        }

        comment.setContent(newContent);
        return toDTO(commentRepository.save(comment));
    }

    // ─── SUPPRESSION ────────────────────────────────────────────────────────────

    public void deleteComment(Long id, String requesterId, boolean isAdmin) {
        Comment comment = findOrThrow(id);

        if (!isAdmin && !comment.getAuthorId().equals(requesterId)) {
            throw new IllegalStateException("Suppression non autorisée");
        }

        commentRepository.delete(comment);
    }

    public void deleteAllByIncident(Long incidentId) {
        commentRepository.deleteByIncidentId(incidentId);
    }

    // ─── PRIVÉ ──────────────────────────────────────────────────────────────────

    private Comment findOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé : " + id));
    }

    private CommentResponseDTO toDTO(Comment c) {
        return CommentResponseDTO.builder()
                .id(c.getId())
                .incidentId(c.getIncidentId())
                .content(c.getContent())
                .authorId(c.getAuthorId())
                .authorName(c.getAuthorName())
                .commentType(c.getCommentType())
                .attachmentUrl(c.getAttachmentUrl())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
