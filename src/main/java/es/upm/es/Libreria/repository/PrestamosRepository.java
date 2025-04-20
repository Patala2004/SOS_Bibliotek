package es.upm.es.Libreria.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import es.upm.es.Libreria.model.Prestamos;

public interface PrestamosRepository extends JpaRepository<Prestamos, Integer> {
    boolean existsByNombre(String nombre);
    Page<Prestamos> findByNombre(@Param("nombre") String nombre, Pageable paginable);
    Page<Prestamos> findByNombreStartsWith(@Param("starts_with") String starts_with, Pageable paginable);
    
}
