package unlar.edu.ar.Tp3.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import unlar.edu.ar.Tp3.models.Cancion;

public class RecomendacionDescubrimiento implements EstrategiaRecomendacion {
    @Override
    public List<Cancion> recomendar(List<Cancion> catalogo, Cancion base) {
        LocalDate hace2Anios = LocalDate.now().minusYears(2);
        return catalogo.stream()
                .filter(c -> c.getReproducciones().get() < 1000
                        && c.getFechaLanzamiento() != null && c.getFechaLanzamiento().isAfter(hace2Anios)
                        && (base == null || c.getGenero() != base.getGenero()))
                .collect(Collectors.toList());
    }
}