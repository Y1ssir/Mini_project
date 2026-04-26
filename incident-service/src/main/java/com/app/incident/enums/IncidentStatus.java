package com.app.incident.enums;

public enum IncidentStatus {
    OPEN,        // Nouveau ticket soumis
    IN_PROGRESS, // Technicien assigné, en cours
    RESOLVED,    // Solution appliquée
    CLOSED,      // Fermé après confirmation client
    REJECTED     // Rejeté (hors périmètre)
}
