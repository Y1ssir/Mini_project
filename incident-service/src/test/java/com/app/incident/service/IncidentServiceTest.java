package com.app.incident.service;

import com.app.incident.dto.IncidentRequestDTO;
import com.app.incident.dto.IncidentResponseDTO;
import com.app.incident.entity.Incident;
import com.app.incident.enums.IncidentPriority;
import com.app.incident.enums.IncidentStatus;
import com.app.incident.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentService incidentService;

    private Incident incident;

    @BeforeEach
    void setUp() {
        incident = Incident.builder()
                .id(1L)
                .title("Test Incident")
                .status(IncidentStatus.OPEN)
                .priority(IncidentPriority.MEDIUM)
                .build();
    }

    @Test
    void shouldCreateIncidentWithOpenStatus() {
        IncidentRequestDTO dto = new IncidentRequestDTO();
        dto.setTitle("New Ticket");
        dto.setPriority(IncidentPriority.HIGH);

        when(incidentRepository.save(any(Incident.class))).thenReturn(incident);

        IncidentResponseDTO result = incidentService.createIncident(dto, "user123");

        assertNotNull(result);
        assertEquals(IncidentStatus.OPEN, result.getStatus());
        verify(incidentRepository).save(any(Incident.class));
    }

    @Test
    void shouldAllowValidStatusTransition() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(incident);

        IncidentResponseDTO result = incidentService.changeStatus(1L, IncidentStatus.IN_PROGRESS, "tech456");

        assertEquals(IncidentStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void shouldThrowExceptionForInvalidTransition() {
        incident.setStatus(IncidentStatus.CLOSED);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        assertThrows(IllegalStateException.class, () -> {
            incidentService.changeStatus(1L, IncidentStatus.OPEN, "tech456");
        });
    }
}
