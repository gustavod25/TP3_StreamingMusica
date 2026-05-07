package unlar.edu.ar.Tp3.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import unlar.edu.ar.Tp3.models.Cancion;
import unlar.edu.ar.Tp3.repository.repositorio;

@Service
public class CancionLogica {
    private final repositorio repo;

    public CancionLogica(repositorio repo) {
        this.repo = repo;
    }

    public Cancion buscarId(int id) {
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
        List<Cancion> ordenada = repo.getCanciones().sort(Comparator.comparing(Cancion::getTitulo))
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

}
