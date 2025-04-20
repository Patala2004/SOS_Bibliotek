package es.upm.es.Libreria.model;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable //Indica que esta clase no es una entidad, sino que se incrusta en otra entidad
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLibroId {
    @NotNull(message = "El libroId es obligatorio y no puede ser null")
    private int libroId;
    private int userId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
