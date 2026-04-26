package com.app.incident.enums;

/**
 * Cycle de vie d'un incident.
 *
 * Workflow autorisé :
 *   OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
 *   OPEN → REJECTED  (hors périmètre)
 *
 * Correspondance avec le cahier des charges :
 *   OPEN        = Nouveau
 *   ASSIGNED    = Assigné    ← ajouté pour respecter le CDC
 *   IN_PROGRESS = En cours
 *   RESOLVED    = Résolu
 *   CLOSED      = Fermé
 *   REJECTED    = Rejeté (hors périmètre)
 */
public enum IncidentStatus {
    OPEN,        // Nouveau ticket soumis
    ASSIGNED,    // Assigné à un technicien
    IN_PROGRESS, // Technicien en cours de traitement
    RESOLVED,    // Solution appliquée, en attente de confirmation
    CLOSED,      // Fermé après confirmation client
    REJECTED     // Rejeté (hors périmètre)
}
