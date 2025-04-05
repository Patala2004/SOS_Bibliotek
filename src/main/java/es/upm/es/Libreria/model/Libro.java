package es.upm.es.Libreria.model;
import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro extends RepresentationModel<Libro>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull(message = "El nombre del libro es obligatorio y no puede ser null")
    @EqualsAndHashCode.Include // campo considerado en el método equals y hash code
    private String nombre;
}
