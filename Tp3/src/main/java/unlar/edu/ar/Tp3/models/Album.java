package unlar.edu.ar.Tp3.models;

import java.util.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Album {

    private String id = UUID.randomUUID().toString();

    private String nombre;
    private String artista;
    private int anioLanzamiento;
    private List<Cancion> canciones;

}
