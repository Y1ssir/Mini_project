package com.app.comment.controller;

import com.app.comment.dto.CommentRequestDTO;
import com.app.comment.dto.CommentResponseDTO;
import com.app.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ── Ajouter un commentaire ───────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<CommentResponseDTO> add(
            @Valid @RequestBody CommentRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        String authorId   = jwt.getSubject();
        String authorName = jwt.getClaimAsString("name");
        boolean isAdmin   = hasRole(jwt, "ADMIN");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(dto, authorId, authorName, isAdmin));
    }

    // ── Commentaire système (interne — API Gateway seule) ────────────────────
    @PostMapping("/system")
    public ResponseEntity<CommentResponseDTO> addSystem(
            @RequestParam Long incidentId,
            @RequestParam String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addSystemComment(incidentId, message));
    }

    // ── Historique d'un incident ─────────────────────────────────────────────
    @GetMapping("/incident/{incidentId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Page<CommentResponseDTO>> byIncident(
            @PathVariable Long incidentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(commentService.getByIncident(incidentId, pageable));
    }

    // ── Détail d'un commentaire ──────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<CommentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getById(id));
    }

    // ── Modifier un commentaire ──────────────────────────────────────────────
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable Long id,
            @RequestParam String content,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(commentService.updateComment(id, content, jwt.getSubject()));
    }

    // ── Supprimer un commentaire ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        commentService.deleteComment(id, jwt.getSubject(), hasRole(jwt, "ADMIN"));
        return ResponseEntity.noContent().build();
    }

    // ── Compter les commentaires d'un incident ────────────────────────────────
    @GetMapping("/incident/{incidentId}/count")
    public ResponseEntity<Long> count(@PathVariable Long incidentId) {
        return ResponseEntity.ok(commentService.countByIncident(incidentId));
    }

    // ── Utilitaire ───────────────────────────────────────────────────────────
    private boolean hasRole(Jwt jwt, String role) {
        // Check dans realm_access.roles
        java.util.Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return false;
        @SuppressWarnings("unchecked")
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        return roles != null && roles.contains(role);
    }
}
