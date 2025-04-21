package es.upm.es.Libreria.model;

import java.time.*;

import java.sql.Date;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Entity
@Data
@Table(name = "user_libro")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserLibro extends RepresentationModel<UserLibro>{
    // @EmbeddedId 
    // private UserLibroId id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER) // Cada instancia UserLibro va a tener un libro
    @JoinColumn(name = "libro_id")
    private Libro libro;

    @ManyToOne(fetch = FetchType.EAGER) // Cada instancia UserLibro va a tener un user
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fechaInicio")
    // java.sql to only save date and not time
    private Date fechaInicio = Date.valueOf(LocalDate.now()); // Fecha en la que se hizo el prestamo (va a ser el instante de creación)

    @Column(name = "fechaFin")
    private Date fechaFin = Date.valueOf(LocalDate.now().plusDays(15)); // Fecha hasta la que se puede devolver el libro sin sancion

    @Column(name = "devuelto", nullable = false)
    private boolean devuelto = false; // Si ha sido devuelto ya o no
}
