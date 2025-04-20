package es.upm.es.Libreria.repository;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import es.upm.es.Libreria.model.UserLibro;

public interface UserLibroRepository extends JpaRepository<UserLibro, Integer>{
    Page<UserLibro> findByUser_Id(int userId, Pageable paginable);
    List<UserLibro> findByUser_Id(int userId);
}
