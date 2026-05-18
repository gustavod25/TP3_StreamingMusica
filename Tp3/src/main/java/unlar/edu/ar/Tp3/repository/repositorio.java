package unlar.edu.ar.Tp3.repository;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import unlar.edu.ar.Tp3.models.Cancion;
import unlar.edu.ar.Tp3.models.Genero;

@Repository
public class repositorio {
    private final List<Cancion> canciones = Collections.synchronizedList(new ArrayList<>());

    public List<Cancion> todo() {
        synchronized (canciones) {
            return new ArrayList<>(canciones);
        }
    }

    public List<Cancion> todo(int page, int size) {
        if (page < 0 || size <= 0)
            return Collections.emptyList();
        int from = page * size;
        synchronized (canciones) {
            if (from >= canciones.size())
                return Collections.emptyList();
            return canciones.subList(from, Math.min(from + size, canciones.size()));
        }
    }

    public Optional<Cancion> buscaId(String id) {
        synchronized (canciones) {
            return canciones.stream().filter(c -> c.getId().equals(id)).findFirst();
        }
    }

    public Cancion guardar(Cancion cancion) {
        if (cancion == null)
            throw new IllegalArgumentException("cancion null");
        synchronized (canciones) {
            Optional<Cancion> existing = canciones.stream().filter(c -> c.getId().equals(cancion.getId())).findFirst();
            if (existing.isPresent()) {
                int idx = canciones.indexOf(existing.get());
                canciones.set(idx, cancion);
            } else {
                canciones.add(cancion);
            }
        }
        return cancion;
    }

    public List<Cancion> buscaTitulo(String titulo) {
        if (titulo == null)
            return todo();
        String q = titulo.toLowerCase();
        synchronized (canciones) {
            return canciones.stream()
                    .filter(c -> c.getTitulo() != null && c.getTitulo().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
    }

    public List<Cancion> buscaPorArtista(String artistaNombre) {
        if (artistaNombre == null)
            return todo();
        String q = artistaNombre.toLowerCase();
        synchronized (canciones) {
            return canciones.stream()
                    .filter(c -> c.getArtista() != null && c.getArtista().getNombre() != null
                            && c.getArtista().getNombre().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
    }

    public List<Cancion> buscaGenero(Genero genero) {
        if (genero == null)
            return todo();
        synchronized (canciones) {
            return canciones.stream()
                    .filter(c -> genero.equals(c.getGenero()))
                    .collect(Collectors.toList());
        }
    }

    public List<Cancion> filtro(Genero genero, String artista, Integer yearFrom, Integer yearTo, Double ratingMin) {
        synchronized (canciones) {
            return canciones.stream().filter(c -> {
                if (genero != null && !genero.equals(c.getGenero()))
                    return false;
                if (artista != null && (c.getArtista() == null || c.getArtista().getNombre() == null
                        || !c.getArtista().getNombre().toLowerCase().contains(artista.toLowerCase())))
                    return false;
                if (yearFrom != null
                        && (c.getFechaLanzamiento() == null || c.getFechaLanzamiento().getYear() < yearFrom))
                    return false;
                if (yearTo != null && (c.getFechaLanzamiento() == null || c.getFechaLanzamiento().getYear() > yearTo))
                    return false;
                if (ratingMin != null && c.getRating() < ratingMin)
                    return false;
                return true;
            }).collect(Collectors.toList());
        }
    }

    public List<Cancion> buscarTopRepro(int n) {
        if (n <= 0)
            return Collections.emptyList();
        synchronized (canciones) {
            return canciones.stream()
                    .sorted(Comparator.comparingInt(c -> -c.getReproducciones().get()))
                    .limit(n)
                    .collect(Collectors.toList());
        }
    }

}
