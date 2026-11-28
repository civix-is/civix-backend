package com.civix.servicios;

import com.civix.events.IncidenciaCambioEvent;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class IncidenciaStreamServicio {

    private final List<SseEmitter> emisores = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "incidencia-sse-heartbeat");
        thread.setDaemon(true);
        return thread;
    });

    public IncidenciaStreamServicio() {
        heartbeatScheduler.scheduleAtFixedRate(this::enviarHeartbeat, 25, 25, TimeUnit.SECONDS);
    }

    public SseEmitter suscribir() {
        SseEmitter emitter = new SseEmitter(0L);
        emisores.add(emitter);

        emitter.onCompletion(() -> emisores.remove(emitter));
        emitter.onTimeout(() -> {
            emisores.remove(emitter);
            emitter.complete();
        });
        emitter.onError(ex -> emisores.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("conectado")
                    .data("ok"));
        } catch (IOException e) {
            emisores.remove(emitter);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void alCambiarIncidencia(IncidenciaCambioEvent event) {
        Object payload = event.incidencia() != null
                ? event.incidencia()
                : Map.of("idIncidencia", event.idIncidencia());

        String nombreEvento = switch (event.tipo()) {
            case "CREADA" -> "incidencia-creada";
            case "ACTUALIZADA" -> "incidencia-actualizada";
            case "ELIMINADA" -> "incidencia-eliminada";
            default -> "incidencia-cambio";
        };

        for (SseEmitter emitter : emisores) {
            try {
                emitter.send(SseEmitter.event()
                        .name(nombreEvento)
                        .data(payload, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                emisores.remove(emitter);
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void enviarHeartbeat() {
        for (SseEmitter emitter : emisores) {
            try {
                emitter.send(SseEmitter.event().comment("ping"));
            } catch (Exception e) {
                emisores.remove(emitter);
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @PreDestroy
    public void cerrar() {
        heartbeatScheduler.shutdownNow();
        for (SseEmitter emitter : emisores) {
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        }
        emisores.clear();
    }
}
