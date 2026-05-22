package unlar.edu.ar.Tp3.controllers;

import java.util.concurrent.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unlar.edu.ar.Tp3.models.Cancion;
import unlar.edu.ar.Tp3.repository.repositorio;

@RestController
@RequestMapping("/api/debug")
public class ControllerConcurrido {
    private final repositorio repo;

    public ControllerConcurrido(repositorio repo) { this.repo = repo; }

    // POST /api/debug/reproducir-paralelo/{id}?n=100
    @PostMapping("/reproducir-paralelo/{id}")
    public ResponseEntity<Object> reproducirParalelo(@PathVariable String id, @RequestParam(defaultValue = "100") int n) throws InterruptedException {
        Cancion c = repo.getCanciones().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        ExecutorService ex = Executors.newFixedThreadPool(Math.min(50, n));
        CountDownLatch latch = new CountDownLatch(n);
        for (int i = 0; i < n; i++) {
            ex.submit(() -> {
                c.incrementarReproduccion();
                latch.countDown();
            });
        }
        latch.await();
        ex.shutdown();
        return ResponseEntity.ok(Map.of("id", c.getId(), "reproducciones", c.getReproducciones().get()));
    }
}