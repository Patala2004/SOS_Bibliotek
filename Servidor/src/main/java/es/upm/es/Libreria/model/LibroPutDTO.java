package es.upm.es.Libreria.model;
import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Embeddable //Indica que esta clase no es una entidad, sino que se incrusta en otra entidad
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibroPutDTO extends RepresentationModel<Libro>{

    private String titulo;

    private String[] autores;

    private Integer edicion;

    private String ISBN;

    private String editorial;
}
