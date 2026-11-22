package com.civix.controladores;

import com.civix.dtos.MejorarDescripcionRequestDTO;
import com.civix.dtos.MejorarDescripcionResponseDTO;
import com.civix.servicios.IaServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", exposedHeaders = "Authorization")
public class IaControlador {

    @Autowired
    private IaServicio iaServicio;

    @PostMapping("/mejorar-descripcion")
    @PreAuthorize("hasRole('CIUDADANO')")
    public ResponseEntity<MejorarDescripcionResponseDTO> mejorar(
            @Valid @RequestBody MejorarDescripcionRequestDTO dto) {
        return ResponseEntity.ok(new MejorarDescripcionResponseDTO(iaServicio.mejorarDescripcion(dto)));
    }
}