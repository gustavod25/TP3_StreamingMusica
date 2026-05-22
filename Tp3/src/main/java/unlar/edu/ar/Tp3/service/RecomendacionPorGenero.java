package unlar.edu.ar.Tp3.service;

import java.util.*;
import java.util.stream.Collectors;
import unlar.edu.ar.Tp3.models.Cancion;

public class RecomendacionPorGenero implements EstrategiaRecomendacion {
    @Override
    public List<Cancion> recomendar(List<Cancion> catalogo, Cancion base) {
        if (base == null || base.getGenero() == null)
            return Collections.emptyList();
        return catalogo.stream()
                .filter(c -> c.getGenero() == base.getGenero() && !c.getId().equals(base.getId()))
                .sorted(Comparator.comparingDouble(Cancion::getRating).reversed())
                .collect(Collectors.toList());
    }
}