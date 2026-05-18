package unlar.edu.ar.Tp3.models;

import java.util.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    private String id = UUID.randomUUID().toString();
    private String titulo;
    private Artista artista;
    private LocalDate fechaLanzamiento;
    private List<Cancion> canciones = new ArrayList<>();
    private Productor productor;

    public void addCancion(Cancion cancion) {
        if (cancion != null && !canciones.contains(cancion)) {
            canciones.add(cancion);
            cancion.setAlbum(this);
            if (cancion.getArtista() != null && this.artista == null)
                this.artista = cancion.getArtista();
            if (cancion.getProductor() != null && this.productor == null)
                this.productor = cancion.getProductor();
        }
    }
}
