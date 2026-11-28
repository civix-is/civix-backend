package com.civix.events;

import com.civix.dtos.IncidenciaDTO;

public record IncidenciaCambioEvent(
        String tipo,
        IncidenciaDTO incidencia,
        Long idIncidencia
) {
}
