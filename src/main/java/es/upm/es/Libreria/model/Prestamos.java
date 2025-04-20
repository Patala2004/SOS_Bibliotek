package es.upm.es.Libreria.model;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamos extends RepresentationModel<Prestamos> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    
}
