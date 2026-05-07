package unlar.edu.ar.Tp3.service;

import java.util.List;
import unlar.edu.ar.Tp3.models.Cancion;

public interface EstrategiaRecomendacion {
    List<Cancion> recomendar(List<Cancion> canciones, Cancion base);
}
