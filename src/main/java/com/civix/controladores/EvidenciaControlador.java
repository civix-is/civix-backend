package com.civix.controladores;

import com.civix.dtos.EvidenciaDTO;
import com.civix.exceptions.BusinessRuleException;
import com.civix.servicios.EvidenciaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/evidencia")
@CrossOrigin(origins = "${ip.frontend}", allowCredentials = "true", exposedHeaders = "Authorization")
public class EvidenciaControlador {
    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");
    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    @Autowired
    private EvidenciaServicio evidenciaServicio;

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<EvidenciaDTO> registrar(@RequestBody EvidenciaDTO evidenciaDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(evidenciaServicio.registrar(evidenciaDTO));
    }

    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CIUDADANO','ADMINISTRADOR')")
    public ResponseEntity<EvidenciaDTO> subir(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("id_incidencia") Long idIncidencia,
            @RequestParam(value = "fase", defaultValue = "Inicial") String fase) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessRuleException("Debe adjuntar una imagen válida");
        }
        if (archivo.getSize() > MAX_FILE_BYTES) {
            throw new BusinessRuleException("La imagen supera el límite máximo de 5MB");
        }
        String contentType = archivo.getContentType() != null ? archivo.getContentType().toLowerCase() : "";
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessRuleException("Solo se permiten imágenes en formato JPG o PNG");
        }

        Files.createDirectories(uploadDir);
        String ext = contentType.equals("image/png") ? ".png" : ".jpg";
        String nombreArchivo = UUID.randomUUID() + ext;
        Path destino = uploadDir.resolve(nombreArchivo).normalize();
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        String urlArchivo = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/evidencia/archivo/")
                .path(nombreArchivo)
                .toUriString();

        EvidenciaDTO dto = new EvidenciaDTO();
        dto.setIdIncidencia(idIncidencia);
        dto.setUrlArchivo(urlArchivo);
        dto.setTipoArchivo("IMAGE");
        dto.setFase(fase);

        return ResponseEntity.status(HttpStatus.CREATED).body(evidenciaServicio.registrar(dto));
    }

    @GetMapping("/archivo/{nombreArchivo}")
    public ResponseEntity<Resource> obtenerArchivo(@PathVariable String nombreArchivo) throws IOException {
        if (nombreArchivo.contains("..") || nombreArchivo.contains("/") || nombreArchivo.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }
        Path archivo = uploadDir.resolve(nombreArchivo).normalize();
        if (!Files.exists(archivo)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(archivo.toUri());
        String contentType = Files.probeContentType(archivo);
        MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .contentType(mediaType)
                .body(resource);
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