package es.upm.es.Libreria.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import es.upm.es.Libreria.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    boolean existsByNombre(String nombre);
    boolean existsByMatricula(Integer matricula);
    Page<User> findByNombre(@Param("nombre") String nombre, Pageable paginable);
    Page<User> findByNombreStartsWith(@Param("starts_with") String starts_with, Pageable paginable);
}
