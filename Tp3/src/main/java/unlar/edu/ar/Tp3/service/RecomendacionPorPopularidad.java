package unlar.edu.ar.Tp3.service;

import java.util.*;
import java.util.stream.Collectors;
import unlar.edu.ar.Tp3.models.Cancion;

public class RecomendacionPorPopularidad implements EstrategiaRecomendacion {
    @Override
    public List<Cancion> recomendar(List<Cancion> catalogo, Cancion base) {
        return catalogo.stream()
                .sorted(Comparator.comparingInt((Cancion c) -> c.getReproducciones().get()).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}