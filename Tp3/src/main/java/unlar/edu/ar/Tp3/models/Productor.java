package unlar.edu.ar.Tp3.models;

import java.util.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Productor {

    private String id = UUID.randomUUID().toString();

    private String nombre;
    private List<Album> albumes;

}
