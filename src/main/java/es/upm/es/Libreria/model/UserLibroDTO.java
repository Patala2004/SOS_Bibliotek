package es.upm.es.Libreria.model;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
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
public class UserLibroDTO{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Libro libro;

    private Date fechaInicio;

    private Date fechaFin;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date fechaDevolucion;

    // constructor
    public UserLibroDTO(UserLibro userLibro){
        this.id = userLibro.getId();
        this.fechaInicio = userLibro.getFechaInicio();
        this.fechaFin = userLibro.getFechaFin();
        this.fechaDevolucion = userLibro.getFechaDevolucion();
        this.libro = userLibro.getLibro();
    }
}