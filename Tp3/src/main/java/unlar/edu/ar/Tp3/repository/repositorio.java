package unlar.edu.ar.Tp3.repository;

import java.util.*;

import org.springframework.stereotype.Repository;

import unlar.edu.ar.Tp3.models.Cancion;

@Repository
public class repositorio {
    private List<Cancion> canciones = new ArrayList<>();

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public void agregarCancion(Cancion cancion) {
        canciones.add(cancion);
    }

}
