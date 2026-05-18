package unlar.edu.ar.Tp3.models;

import java.util.*;

import lombok.Data;

@Data
public class Artista {

    private String id = UUID.randomUUID().toString();
    private String nombre;
    private List<Album> albumes = new ArrayList<>();
    private List<Cancion> canciones = new ArrayList<>();

    public void addAlbum(Album album) {
        if (album != null && !albumes.contains(album)) {
            albumes.add(album);
            album.setArtista(this);
        }
    }

    public void addCancion(Cancion cancion) {
        if (cancion != null && !canciones.contains(cancion)) {
            canciones.add(cancion);
            cancion.setArtista(this);
            if (cancion.getAlbum() != null && !albumes.contains(cancion.getAlbum())) {
                albumes.add(cancion.getAlbum());
            }
        }
    }

}
