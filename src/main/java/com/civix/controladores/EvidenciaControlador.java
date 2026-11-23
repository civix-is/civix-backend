package com.civix.controladores;

import com.civix.dtos.EvidenciaDTO;
import com.civix.servicios.EvidenciaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evidencia")
@CrossOrigin(origins = "${ip.frontend}", allowCredentials = "true", exposedHeaders = "Authorization")
public class EvidenciaControlador {
    @Autowired
    private EvidenciaServicio evidenciaServicio;

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<EvidenciaDTO> registrar(@RequestBody EvidenciaDTO evidenciaDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(evidenciaServicio.registrar(evidenciaDTO));
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<EvidenciaDTO>> listar(){
        return ResponseEntity.ok(evidenciaServicio.listar());
    }

    @GetMapping("/buscar-por-incidencia")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<List<EvidenciaDTO>> buscarPorIncidencia(@RequestParam("id_incidencia") Long idIncidencia){
        return ResponseEntity.ok(evidenciaServicio.buscarPorIncidencia(idIncidencia));
    }

    @DeleteMapping("/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void eliminar(@RequestParam("id_evidencia") Long id){
        evidenciaServicio.eliminar(id);
    }
}