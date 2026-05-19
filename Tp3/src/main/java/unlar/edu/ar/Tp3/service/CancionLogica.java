package unlar.edu.ar.Tp3.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import unlar.edu.ar.Tp3.models.Cancion;
import unlar.edu.ar.Tp3.models.Genero;
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

    public void reproducirCancion(String id) {
        Cancion cancion = buscarId(id);
        if (cancion != null) {
        }
        if (cancion.getReproducciones() != null) {
            cancion.getReproducciones().incrementAndGet();
        }
        String artista = (cancion.getArtista() != null) ? cancion.getArtista().getNombre() : "desconocido";
        System.out.println("Reproduciendo: " + cancion.getTitulo() + " de " + artista);
    }else

    {
        System.out.println("Canción no encontrada.");
    }
    }

    // Búsqueda binaria por título
    public Cancion busquedaBinariaTitulo(String titulo) {
        List<Cancion> ordenada = repo.getCanciones().stream()
                .sorted(Comparator.comparing(Cancion::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        int inicio = 0;
        int fin = ordenada.size() - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            Cancion cancionMedio = ordenada.get(medio);
            int comparacion = cancionMedio.getTitulo().compareToIgnoreCase(titulo);

            if (comparacion == 0)
                return cancionMedio;
            if (comparacion < 0)
                inicio = medio + 1;
            else
                fin = medio - 1;
        }
        return null;
    }

    private EstrategiaRecomendacion estrategia;

    public void setEstrategia(EstrategiaRecomendacion estrategia) {
        this.estrategia = estrategia;
    }

    public List<Cancion> recomendarCanciones(List<Cancion> canciones, Cancion base) {
        if (estrategia == null)
            return Collections.emptyList();
        return estrategia.recomendar(canciones, base);
    }

    // Filtrado compuesto con Streams
    public List<Cancion> filtrarCanciones(List<Cancion> canciones, Genero genero, Integer yearFrom, Integer yearTo,
            Double ratingMin) {
        return canciones.stream()
                .filter(c -> {
                    if (genero != null && c.getGenero() != genero)
                        return false;
                    if (yearFrom != null
                            && (c.getFechaLanzamiento() == null || c.getFechaLanzamiento().getYear() < yearFrom))
                        return false;
                    if (yearTo != null
                            && (c.getFechaLanzamiento() == null || c.getFechaLanzamiento().getYear() > yearTo))
                        return false;
                    if (ratingMin != null && c.getRating() < ratingMin)
                        return false;
                    return true;
                })
                .collect(Collectors.toList());
    }

    // Top N por reproducciones
    public List<Cancion> topNCanciones(int n) {
        return repo.getCanciones().stream()
                .sorted(Comparator.comparingInt((Cancion c) -> c.getReproducciones().get()).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<Genero, Double> promedioDuracionPorGenero() {
        return repo.getCanciones().stream()
                .filter(c -> c.getGenero() != null)
                .collect(Collectors.groupingBy(Cancion::getGenero,
                        Collectors.averagingInt(Cancion::getDuracionSegundos)));
    }

    // Artista más popular (por suma de reproducciones)
    public Optional<String> artistaMasPopular() {
        Map<String, Integer> mapa = repo.getCanciones().stream()
                .filter(c -> c.getArtista() != null && c.getArtista().getNombre() != null)
                .collect(Collectors.groupingBy(c -> c.getArtista().getNombre(),
                        Collectors.summingInt(c -> c.getReproducciones().get())));

        return mapa.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    // Distribución por décadas
    public Map<Integer, List<Cancion>> agruparPorDecada() {
        return repo.getCanciones().stream()
                .filter(c -> c.getFechaLanzamiento() != null)
                .collect(Collectors.groupingBy(c -> (c.getFechaLanzamiento().getYear() / 10) * 10));
    }

    public List<Cancion> busquedaMultiple(Genero genero, double ratingMinimo, int añoMinimo) {
        return repo.getCanciones().stream()
                .filter(c -> c.getGenero() == genero && c.getRating() >= ratingMinimo && c.getFechaLanzamiento() != null
                        && c.getFechaLanzamiento().getYear() >= añoMinimo)
                .sorted(Comparator.comparing(Cancion::getTitulo))
                .collect(Collectors.toList());
    }

    // Generar playlist aproximada (backtracking, intenta alcanzar target exacto o
    // la mejor aproximación)
    public List<Cancion> generarPlayList(int minutos) {
        int objetivoEnSeg = minutos * 60;
        List<Cancion> canciones = new ArrayList<>(repo.getCanciones());
        canciones.sort(Comparator.comparingInt(Cancion::getDuracionSegundos).reversed());

        solucionOptima mejor = new solucionOptima();
        retroceder(canciones, 0, new ArrayList<>(), 0, objetivoEnSeg, mejor);

        return mejor.mejorLista;
    }

    private void retroceder(List<Cancion> canciones, int indice, List<Cancion> listaActual, int sumaActual,
            int objetivoEnSeg, solucionOptima mejor) {
        if (sumaActual > objetivoEnSeg)
            return;
        if (sumaActual == objetivoEnSeg) {
            mejor.mejorLista = new ArrayList<>(listaActual);
            mejor.mejorSuma = sumaActual;
            return;
        }
        if (sumaActual > mejor.mejorSuma) {
            mejor.mejorLista = new ArrayList<>(listaActual);
            mejor.mejorSuma = sumaActual;
        }
        if (indice >= canciones.size())
            return;

        for (int i = indice; i < canciones.size(); i++) {
            Cancion c = canciones.get(i);
            listaActual.add(c);
            retroceder(canciones, i + 1, listaActual, sumaActual + c.getDuracionSegundos(), objetivoEnSeg, mejor);
            listaActual.remove(listaActual.size() - 1);
            if (mejor.mejorSuma == objetivoEnSeg)
                return;
        }
    }

    private static class solucionOptima {
        List<Cancion> mejorLista = new ArrayList<>();
        int mejorSuma = 0;
    }
}