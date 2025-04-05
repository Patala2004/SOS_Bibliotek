package es.upm.es.Libreria.model;
import java.io.Serializable;
import java.util.Date;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends RepresentationModel<User>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "El nombre del usuario es obligatorio y no puede ser null")
    private String nombre;

    @NotNull(message = "La matricula del usuario es obligatoria y no puede ser null")
    private Integer matricula;

    @NotNull(message = "La fecha de nacimiento del usuario es obligatoria y no puede ser null")
    private Date fechaNacimiento;

    @NotNull(message = "El correo del usuario es obligatorio y no puede ser null")
    private String email;

}
