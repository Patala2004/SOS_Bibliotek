package es.upm.es.Libreria.model;

import java.util.Date;

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

    @NotNull(message = "El userId es obligatorio y no puede ser null")
    private int userId;

    // Fecha inicio y fin se pone automaticamente en el codigo basado en cuando recibe la peticion

    @NotNull(message = "La fecha de nacimiento del usuario es obligatoria y no puede ser null")
    private boolean devuelto; // Si ha sido devuelto ya o no
}
