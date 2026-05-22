package unlar.edu.ar.Tp3.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unlar.edu.ar.Tp3.models.Artista;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {
    private final List<Artista> artistas = Collections.synchronizedList(new ArrayList<>());

    @GetMapping
    public List<Artista> listar() {
        synchronized (artistas) {
            return new ArrayList<>(artistas);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artista> obtener(@PathVariable String id) {
        synchronized (artistas) {
            return artistas.stream().filter(a -> a.getId().equals(id)).findFirst()
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/buscar")
    public List<Artista> buscar(@RequestParam String nombre) {
        synchronized (artistas) {
            return artistas.stream()
                    .filter(a -> a.getNombre() != null && a.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    @PostMapping
    public ResponseEntity<Artista> crear(@RequestBody Artista artista) {
        if (artista.getId() == null)
            artista.setId(UUID.randomUUID().toString());
        artistas.add(artista);
        return ResponseEntity.status(201).body(artista);
    }
}