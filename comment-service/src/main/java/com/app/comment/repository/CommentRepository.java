package com.app.comment.repository;

import com.app.comment.entity.Comment;
import com.app.comment.entity.Comment.CommentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Historique complet d'un incident (trié par date)
    Page<Comment> findByIncidentIdOrderByCreatedAtAsc(Long incidentId, Pageable pageable);

    // Commentaires par type sur un incident
    List<Comment> findByIncidentIdAndCommentType(Long incidentId, CommentType type);

    // Nombre de commentaires par incident
    long countByIncidentId(Long incidentId);

    // Supprimer tous les commentaires d'un incident (cascade logique)
    void deleteByIncidentId(Long incidentId);
}
