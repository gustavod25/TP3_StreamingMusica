package unlar.edu.ar.Tp3.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unlar.edu.ar.Tp3.models.Productor;

@RestController
@RequestMapping("/api/productores")
public class ProductorController {
    private final List<Productor> productores = Collections.synchronizedList(new ArrayList<>());

    @GetMapping
    public List<Productor> listar() {
        synchronized (productores) {
            return new ArrayList<>(productores);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Productor> obtener(@PathVariable String id) {
        synchronized (productores) {
            return productores.stream().filter(p -> p.getId().equals(id)).findFirst()
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/buscar")
    public List<Productor> buscar(@RequestParam String nombre) {
        synchronized (productores) {
            return productores.stream()
                    .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    @PostMapping
    public ResponseEntity<Productor> crear(@RequestBody Productor p) {
        if (p.getId() == null)
            p.setId(UUID.randomUUID().toString());
        productores.add(p);
        return ResponseEntity.status(201).body(p);
    }
}