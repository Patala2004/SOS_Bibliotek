package es.upm.es.Libreria.model;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Set;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Embeddable //Indica que esta clase no es una entidad, sino que se incrusta en otra entidad
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPutDTO extends RepresentationModel<User>{
    private String nombre;

    private Integer matricula;

    private Date fechaNacimiento;

    private String email;
}
