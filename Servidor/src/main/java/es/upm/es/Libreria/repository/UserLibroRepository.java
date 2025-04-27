package es.upm.es.Libreria.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import es.upm.es.Libreria.model.UserLibro;

public interface UserLibroRepository extends JpaRepository<UserLibro, Integer>{
    Page<UserLibro> findByUser_Id(int userId, Pageable paginable);
    List<UserLibro> findByUser_Id(int userId);
    List<UserLibro> findByLibro_Id(int libroId);

    Page<UserLibro> findByUser_IdAndFechaDevolucionIsNotNull(int userId, Pageable paginable);
    Page<UserLibro> findByUser_IdAndFechaDevolucionIsNullAndFechaInicioBetween(int userId, Date from, Date to, Pageable paginable);
}
