package com.app.incident.controller;

import com.app.incident.dto.IncidentRequestDTO;
import com.app.incident.dto.IncidentResponseDTO;
import com.app.incident.enums.IncidentStatus;
import com.app.incident.service.IncidentService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    // ── CLIENT : créer un incident ───────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<IncidentResponseDTO> create(
            @Valid @RequestBody IncidentRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject(); // sub = Keycloak user ID
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentService.createIncident(dto, userId));
    }

    // ── CLIENT : mes incidents ───────────────────────────────────────────────
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Page<IncidentResponseDTO>> myIncidents(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(incidentService.getMyIncidents(jwt.getSubject(), pageable));
    }

    // ── ADMIN : tous les incidents ───────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<IncidentResponseDTO>> getAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(incidentService.getAllIncidents(pageable));
    }

    // ── ADMIN : filtrer par statut ───────────────────────────────────────────
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<IncidentResponseDTO>> byStatus(
            @PathVariable IncidentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(incidentService.getByStatus(status, pageable));
    }

    // ── Détail ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<IncidentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getById(id));
    }

    // ── CLIENT/ADMIN : modifier le contenu ───────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<IncidentResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody IncidentRequestDTO dto) {
        return ResponseEntity.ok(incidentService.updateIncident(id, dto));
    }

    // ── ADMIN : changer le statut (workflow) ─────────────────────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncidentResponseDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam IncidentStatus newStatus,
            @AuthenticationPrincipal Jwt jwt) {
        String techId = jwt.getSubject();
        return ResponseEntity.ok(incidentService.changeStatus(id, newStatus, techId));
    }

    // ── ADMIN : assigner un technicien ───────────────────────────────────────
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncidentResponseDTO> assign(
            @PathVariable Long id,
            @RequestParam String technicianId) {
        return ResponseEntity.ok(incidentService.assignTechnician(id, technicianId));
    }

    // ── ADMIN : supprimer ───────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    // ── CHATBOT (Member 4) : recherche par mot-clé ───────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<IncidentResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(incidentService.searchByKeyword(keyword));
    }

    // ── CHATBOT (Member 4) : incidents similaires résolus ────────────────────
    @GetMapping("/similar")
    public ResponseEntity<List<IncidentResponseDTO>> similar(
            @RequestParam String category,
            @PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(incidentService.getSimilarResolved(category, pageable));
    }

    // ── ADMIN : statistiques dashboard ──────────────────────────────────────
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> stats() {
        return ResponseEntity.ok(incidentService.getStatsByStatus());
    }
}
