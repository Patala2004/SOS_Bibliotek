package es.upm.es.Libreria.model;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
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

    // "fecha_inicio": "string", 

    // "fecha_fin": "string", 

    // "id_prestamo": "string", 

    // "devolucion": "boolean"
}
