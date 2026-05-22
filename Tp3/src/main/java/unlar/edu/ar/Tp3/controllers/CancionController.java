package unlar.edu.ar.Tp3.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unlar.edu.ar.Tp3.models.*;
import unlar.edu.ar.Tp3.repository.repositorio;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    private final repositorio repo;

    public CancionController(repositorio repo) {
        this.repo = repo;
    }

    // 12. GET /api/canciones - listar todas (pag optional)
    @GetMapping
    public ResponseEntity<List<Cancion>> listar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        List<Cancion> all;
        synchronized (repo.getCanciones()) {
            all = new ArrayList<>(repo.getCanciones());
        }

        if (page == null || size == null)
            return ResponseEntity.ok(all);

        if (page < 0 || size <= 0)
            return ResponseEntity.badRequest().build();
        int from = page * size;
        if (from >= all.size())
            return ResponseEntity.ok(Collections.emptyList());
        int to = Math.min(from + size, all.size());
        return ResponseEntity.ok(all.subList(from, to));
    }

    // 13. GET /api/canciones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cancion> obtenerPorId(@PathVariable String id) {
        synchronized (repo.getCanciones()) {
            return repo.getCanciones().stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    // 14. GET /api/canciones/buscar?titulo=xxx&artista=yyy
    @GetMapping("/buscar")
    public ResponseEntity<List<Cancion>> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String artista) {

        synchronized (repo.getCanciones()) {
            List<Cancion> results = repo.getCanciones().stream()
                    .filter(c -> {
                        boolean ok = true;
                        if (titulo != null && !titulo.isBlank()) {
                            ok = ok && c.getTitulo() != null
                                    && c.getTitulo().toLowerCase().contains(titulo.toLowerCase());
                        }
                        if (artista != null && !artista.isBlank()) {
                            ok = ok && c.getArtista() != null && c.getArtista().getNombre() != null
                                    && c.getArtista().getNombre().toLowerCase().contains(artista.toLowerCase());
                        }
                        return ok;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(results);
        }
    }

    // 15. POST /api/canciones/{id}/reproducir - incrementar contador
    @PostMapping("/{id}/reproducir")
    public ResponseEntity<Map<String, Object>> reproducir(@PathVariable String id) {
        synchronized (repo.getCanciones()) {
            Optional<Cancion> opt = repo.getCanciones().stream().filter(c -> c.getId().equals(id)).findFirst();
            if (opt.isEmpty())
                return ResponseEntity.notFound().build();
            Cancion c = opt.get();
            int contador = c.getReproducciones().incrementAndGet();
            Map<String, Object> body = new HashMap<>();
            body.put("id", c.getId());
            body.put("titulo", c.getTitulo());
            body.put("reproducciones", contador);
            return ResponseEntity.ok(body);
        }
    }

    // Top N - GET /api/canciones/top?n=10
    @GetMapping("/top")
    public ResponseEntity<List<Cancion>> top(@RequestParam(defaultValue = "10") int n) {
        synchronized (repo.getCanciones()) {
            List<Cancion> list = repo.getCanciones().stream()
                    .sorted(Comparator.comparingInt((Cancion x) -> x.getReproducciones().get()).reversed())
                    .limit(n)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(list);
        }
    }

    // Playlist automática (aproximación greedy) - POST
    // /api/canciones/playlist?minutos=30
    @PostMapping("/playlist")
    public ResponseEntity<List<Cancion>> generarPlaylist(@RequestParam int minutos) {
        if (minutos <= 0)
            return ResponseEntity.badRequest().build();
        int target = minutos * 60;
        List<Cancion> catalogo;
        synchronized (repo.getCanciones()) {
            catalogo = new ArrayList<>(repo.getCanciones());
        }
        catalogo.sort(Comparator.comparingInt(Cancion::getDuracionSegundos).reversed());
        List<Cancion> playlist = new ArrayList<>();
        int sum = 0;
        for (Cancion c : catalogo) {
            if (sum + c.getDuracionSegundos() <= target) {
                playlist.add(c);
                sum += c.getDuracionSegundos();
            }
            if (sum == target)
                break;
        }
        return ResponseEntity.ok(playlist);
    }

}