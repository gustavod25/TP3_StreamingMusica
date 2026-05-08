package unlar.edu.ar.Tp3.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import unlar.edu.ar.Tp3.models.Cancion;
import unlar.edu.ar.Tp3.models.Cancion.Genero;
import unlar.edu.ar.Tp3.repository.repositorio;

@Service
public class CancionLogica {
    private final repositorio repo;

    public CancionLogica(repositorio repo) {
        this.repo = repo;
    }

    public Cancion buscarId(String id) {
        return repo.getCanciones().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

    }

    public void reproducirCancion(int id) {
        Cancion cancion = buscarId(id);
        if (cancion != null) {
            cancion.getReproducciones().incrementAndGet();
            System.out.println("Reproduciendo: " + cancion.getTitulo() + " de " + cancion.getArtista());
        } else {
            System.out.println("Canción no encontrada.");
        }
    }

    public Cancion busquedaBinariaTitulo(String titulo) {
        List<Cancion> ordenada = repo.getCanciones().stream()
                .sorted(Comparator.comparing(Cancion::getTitulo))
                .collect(Collectors.toList());

        int inicio = 0;
        int fin = ordenada.size() - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            Cancion cancionMedio = ordenada.get(medio);
            int comparacion = cancionMedio.getTitulo().compareToIgnoreCase(titulo);

            if (comparacion == 0) {
                return cancionMedio;
            } else if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return null;

    }

    private EstrategiaRecomendacion estrategia;

    public void setEstrategia(EstrategiaRecomendacion estrategia) {
        this.estrategia = estrategia;
    }

    public List<Cancion> recomendarCanciones(List<Cancion> canciones, Cancion base) {
        if (estrategia == null) {
            return Collections.emptyList();
        }
        return estrategia.recomendar(canciones, base);
    }

    public List<Cancion> filtrarCanciones(List<Cancion> canciones,Genero genero, double ratingMinimo) {
        return canciones.stream()
                .filter(c -> c.getGenero() == genero && c.getRating() >= ratingMinimo)
                .collect(Collectors.toList());
        
    }




    public List<Cancion> top10Canciones (List<Cancion> canciones) {
        return canciones.stream()
                .sorted(Comparator.comparingInt(c -> c.getReproducciones().get()).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }






    public Optional<Cancion> top10Artistas(List<Cancion> canciones) {
        return canciones.stream()
                .max(Comparator.comparingInt(c -> c.getReproducciones().get()));
    }


    public Map<Integer, List<Cancion>> agruparPorDecada(List<Cancion> canciones) {
        return canciones.stream()
                .collect(Collectors.groupingBy(c -> (c.getFechaLanzamiento().getYear() / 10) * 10));
    }





    public List<Cancion> busquedaMultiple(Genero genero, double ratingMinimo, int añoMinimo) {
        return repositorio.getCanciones().stream().filter(c -> c.getGenero() == genero && c.getRating() >= ratingMinimo && c.getFechaLanzamiento().getYear() >= añoMinimo)
                .sorted(Comparator.comparing(Cancion::getTitulo))
                .collect(Collectors.toList());
    }
}
