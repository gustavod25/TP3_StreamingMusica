package unlar.edu.ar.Tp3.models;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

@Data
public class Cancion {
    private String id = UUID.randomUUID().toString();
    private String titulo;
    private Album album;
    private Artista artista;
    private Productor productor;
    private Genero genero;
    private int duracionSegundos;
    private AtomicInteger reproducciones = new AtomicInteger(0);
    private double rating;
    private LocalDate fechaLanzamiento;

    public int incrementarReproduccion() {
        return this.reproducciones.incrementAndGet();
    }
}
