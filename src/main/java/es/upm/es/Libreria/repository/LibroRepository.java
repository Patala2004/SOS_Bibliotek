package es.upm.es.Libreria.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import es.upm.es.Libreria.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Integer>{
    
    boolean existsByTitulo(String titulo);
    Page<Libro> findByTitulo(@Param("titulo") String titulo, Pageable paginable);
    Page<Libro> findByTituloStartsWith(@Param("starts_with") String starts_with, Pageable paginable);
    Page<Libro> findByTituloStartsWithAndDisponible(@Param("starts_with") String starts_with, @Param("devuelto") Boolean devuelto, Pageable paginable);
    Page<Libro> findByDisponible(@Param("devuelto") Boolean devuelto, Pageable paginable);
}
