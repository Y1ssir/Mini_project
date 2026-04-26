package com.app.incident.repository;

import com.app.incident.entity.Incident;
import com.app.incident.enums.IncidentStatus;
import com.app.incident.enums.IncidentPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // Tous les incidents d'un utilisateur
    Page<Incident> findByCreatedBy(String createdBy, Pageable pageable);

    // Incidents par statut (pour le dashboard admin)
    Page<Incident> findByStatus(IncidentStatus status, Pageable pageable);

    // Incidents assignés à un technicien
    List<Incident> findByAssignedTo(String assignedTo);

    // Recherche full-text pour le chatbot (Member 4)
    @Query("SELECT i FROM Incident i WHERE " +
           "LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Incident> searchByKeyword(@Param("keyword") String keyword);

    // Incidents similaires (pour le chatbot — recherche par catégorie + statut RESOLVED)
    @Query("SELECT i FROM Incident i WHERE " +
           "i.category = :category AND i.status = 'RESOLVED' " +
           "ORDER BY i.resolvedAt DESC")
    List<Incident> findSimilarResolvedIncidents(@Param("category") String category, Pageable pageable);

    // Statistiques par statut
    long countByStatus(IncidentStatus status);

    // Statistiques par priorité
    long countByPriority(IncidentPriority priority);
}
