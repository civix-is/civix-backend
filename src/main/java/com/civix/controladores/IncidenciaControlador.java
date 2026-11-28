package com.civix.controladores;

import com.civix.dtos.IncidenciaActualizarDTO;
import com.civix.dtos.IncidenciaDTO;
import com.civix.dtos.IncidenciaRegistroDTO;
import com.civix.servicios.IncidenciaServicio;
import com.civix.servicios.IncidenciaStreamServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/incidencia")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", exposedHeaders = "Authorization")
public class IncidenciaControlador {
    @Autowired
    private IncidenciaServicio incidenciaServicio;
    @Autowired
    private IncidenciaStreamServicio incidenciaStreamServicio;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public SseEmitter stream() {
        return incidenciaStreamServicio.suscribir();
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<IncidenciaDTO> registrar(@RequestBody IncidenciaRegistroDTO incidenciaRegistroDTO,
                                                   Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidenciaServicio.registrar(incidenciaRegistroDTO, authentication.getName()));
    }

    @GetMapping("/mis-reportes")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<List<IncidenciaDTO>> listarMisReportes(Authentication authentication) {
        return ResponseEntity.ok(incidenciaServicio.listarMisReportes(authentication.getName()));
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<List<IncidenciaDTO>> listar() {
        return ResponseEntity.ok(incidenciaServicio.listar());
    }

    @GetMapping("/buscar-por-id")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<IncidenciaDTO> buscarPorId(@RequestParam("id_incidencia") Long id) {
        return ResponseEntity.ok(incidenciaServicio.buscarPorId(id));
    }

    @GetMapping("/buscar-por-estado-atencion")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<IncidenciaDTO>> buscarPorEstadoAtencion(@RequestParam("estado_atencion") String estadoAtencion) {
        return ResponseEntity.ok(incidenciaServicio.buscarPorEstadoAtencion(estadoAtencion));
    }

    @PutMapping("/actualizar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<IncidenciaDTO> actualizar(@RequestBody IncidenciaActualizarDTO incidenciaActualizarDTO,
                                                    Authentication authentication){
        return ResponseEntity.ok(incidenciaServicio.actualizar(incidenciaActualizarDTO, authentication.getName()));
    }

    @DeleteMapping("/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@RequestParam("id_incidencia") Long id){
        incidenciaServicio.eliminar(id);
    }


    
}