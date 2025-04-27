package es.upm.es.Libreria.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.upm.es.Libreria.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Integer>{
    
    boolean existsByTitulo(String titulo);
    // Page<Libro> findByTitulo(@Param("titulo") String titulo, Pageable paginable);
    // Page<Libro> findByTituloContaining(@Param("containing") String titulo, Pageable paginable);
    // Page<Libro> findByTituloStartsWith(@Param("starts_with") String starts_with, Pageable paginable);
    // Page<Libro> findByTituloStartsWithAndTituloContaining(@Param("starts_with") String starts_with, @Param("containing") String containing, Pageable paginable);
    // Page<Libro> findByTituloStartsWithAndDisponible(@Param("starts_with") String starts_with, @Param("devuelto") Boolean devuelto, Pageable paginable);
    // Page<Libro> findByTituloStartsWithAndTituloContainingAndDisponible(@Param("starts_with") String starts_with, @Param("containing") String containing, @Param("devuelto") Boolean devuelto, Pageable paginable);
    // Page<Libro> findByDisponible(@Param("devuelto") Boolean devuelto, Pageable paginable);
    @Query("""
    SELECT l FROM Libro l
    WHERE (:startsWith IS NULL OR l.titulo LIKE :startsWith)
    AND (:containing IS NULL OR l.titulo LIKE :containing)
    AND (:disponible IS NULL OR l.disponible = :disponible)
    """)
    Page<Libro> searchLibros(
        @Param("startsWith") String startsWith,
        @Param("containing") String containing,
        @Param("disponible") Boolean disponible,
        Pageable pageable
    );
}
