package unlar.edu.ar.Tp3.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unlar.edu.ar.Tp3.models.Album;

@RestController
@RequestMapping("/api/albumes")
public class AlbumController {
    private final List<Album> albumes = Collections.synchronizedList(new ArrayList<>());

    @GetMapping
    public List<Album> listar() {
        synchronized (albumes) {
            return new ArrayList<>(albumes);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> obtener(@PathVariable String id) {
        synchronized (albumes) {
            return albumes.stream().filter(a -> a.getId().equals(id)).findFirst()
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/buscar")
    public List<Album> buscar(@RequestParam String titulo) {
        synchronized (albumes) {
            return albumes.stream()
                    .filter(a -> a.getTitulo() != null && a.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    @PostMapping
    public ResponseEntity<Album> crear(@RequestBody Album album) {
        if (album.getId() == null)
            album.setId(UUID.randomUUID().toString());
        albumes.add(album);
        return ResponseEntity.status(201).body(album);
    }
}