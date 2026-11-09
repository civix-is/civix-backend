package com.civix.controladores;

import com.civix.dtos.CalificacionDTO;
import com.civix.servicios.CalificacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calificacion")
public class CalificacionControlador {
    @Autowired
    private CalificacionServicio calificacionServicio;

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('CIUDADANO')")
    public ResponseEntity<CalificacionDTO> registrar(@RequestBody CalificacionDTO calificacionDTO,
                                                     Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calificacionServicio.registrar(calificacionDTO, authentication.getName()));
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<CalificacionDTO>> listar(){
        return ResponseEntity.ok(calificacionServicio.listar());
    }

    @GetMapping("/buscar-por-id")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CalificacionDTO> buscarPorId(@RequestParam("id_calificacion") Long id) {
        return ResponseEntity.ok(calificacionServicio.buscarPorId(id));
    }

    @GetMapping("/buscar-por-incidencia")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<CalificacionDTO> buscarPorIncidencia(@RequestParam("id_incidencia") Long idIncidencia) {
        return ResponseEntity.ok(calificacionServicio.buscarPorIncidencia(idIncidencia));
    }
}