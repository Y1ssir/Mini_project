package com.app.incident.service;

import com.app.incident.dto.IncidentRequestDTO;
import com.app.incident.dto.IncidentResponseDTO;
import com.app.incident.entity.Incident;
import com.app.incident.enums.IncidentStatus;
import com.app.incident.repository.IncidentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;

    // ─── CRÉATION ───────────────────────────────────────────────────────────────

    public IncidentResponseDTO createIncident(IncidentRequestDTO dto, String createdByUserId) {
        Incident incident = Incident.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .category(dto.getCategory())
                .attachmentUrl(dto.getAttachmentUrl())
                .status(IncidentStatus.OPEN)   // statut initial toujours OPEN
                .createdBy(createdByUserId)
                .build();

        return toDTO(incidentRepository.save(incident));
    }

    // ─── LECTURE ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public IncidentResponseDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getAllIncidents(Pageable pageable) {
        return incidentRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getMyIncidents(String userId, Pageable pageable) {
        return incidentRepository.findByCreatedBy(userId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> getByStatus(IncidentStatus status, Pageable pageable) {
        return incidentRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> searchByKeyword(String keyword) {
        return incidentRepository.searchByKeyword(keyword)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getSimilarResolved(String category, Pageable pageable) {
        return incidentRepository.findSimilarResolvedIncidents(category, pageable)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── MISE À JOUR ────────────────────────────────────────────────────────────

    public IncidentResponseDTO updateIncident(Long id, IncidentRequestDTO dto) {
        Incident incident = findOrThrow(id);
        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());
        incident.setPriority(dto.getPriority());
        incident.setCategory(dto.getCategory());
        if (dto.getAttachmentUrl() != null) {
            incident.setAttachmentUrl(dto.getAttachmentUrl());
        }
        return toDTO(incidentRepository.save(incident));
    }

    // ─── WORKFLOW / STATUTS ──────────────────────────────────────────────────────

    /**
     * Transition de statut avec validation des transitions autorisées.
     * Workflow : OPEN → IN_PROGRESS → RESOLVED → CLOSED
     *            OPEN → REJECTED
     */
    public IncidentResponseDTO changeStatus(Long id, IncidentStatus newStatus, String technicianId) {
        Incident incident = findOrThrow(id);
        validateTransition(incident.getStatus(), newStatus);

        incident.setStatus(newStatus);

        // Assignation automatique lors de la prise en charge
        if (newStatus == IncidentStatus.IN_PROGRESS && technicianId != null) {
            incident.setAssignedTo(technicianId);
        }

        // Horodatage de la résolution
        if (newStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        return toDTO(incidentRepository.save(incident));
    }

    public IncidentResponseDTO assignTechnician(Long id, String technicianId) {
        Incident incident = findOrThrow(id);
        incident.setAssignedTo(technicianId);
        return toDTO(incidentRepository.save(incident));
    }

    // ─── SUPPRESSION ────────────────────────────────────────────────────────────

    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new EntityNotFoundException("Incident non trouvé : " + id);
        }
        incidentRepository.deleteById(id);
    }

    // ─── STATISTIQUES ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Long> getStatsByStatus() {
        return Map.of(
            "OPEN",        incidentRepository.countByStatus(IncidentStatus.OPEN),
            "IN_PROGRESS", incidentRepository.countByStatus(IncidentStatus.IN_PROGRESS),
            "RESOLVED",    incidentRepository.countByStatus(IncidentStatus.RESOLVED),
            "CLOSED",      incidentRepository.countByStatus(IncidentStatus.CLOSED),
            "REJECTED",    incidentRepository.countByStatus(IncidentStatus.REJECTED)
        );
    }

    // ─── PRIVÉ ──────────────────────────────────────────────────────────────────

    private Incident findOrThrow(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident non trouvé : " + id));
    }

    /**
     * Valide que la transition demandée est autorisée selon le workflow.
     */
    private void validateTransition(IncidentStatus current, IncidentStatus target) {
        boolean valid = switch (current) {
            case OPEN        -> target == IncidentStatus.IN_PROGRESS || target == IncidentStatus.REJECTED;
            case IN_PROGRESS -> target == IncidentStatus.RESOLVED;
            case RESOLVED    -> target == IncidentStatus.CLOSED;
            default          -> false; // CLOSED et REJECTED sont des états terminaux
        };

        if (!valid) {
            throw new IllegalStateException(
                "Transition interdite : " + current + " → " + target
            );
        }
    }

    private IncidentResponseDTO toDTO(Incident i) {
        return IncidentResponseDTO.builder()
                .id(i.getId())
                .title(i.getTitle())
                .description(i.getDescription())
                .status(i.getStatus())
                .priority(i.getPriority())
                .createdBy(i.getCreatedBy())
                .assignedTo(i.getAssignedTo())
                .category(i.getCategory())
                .attachmentUrl(i.getAttachmentUrl())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .resolvedAt(i.getResolvedAt())
                .build();
    }
}
