package es.upm.es.Libreria.service;

import java.sql.Date;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import es.upm.es.Libreria.repository.LibroRepository;
import es.upm.es.Libreria.repository.UserLibroRepository;
import lombok.*;
import es.upm.es.Libreria.model.*;

@Service
@AllArgsConstructor
public class UserLibroService {
    private final UserLibroRepository repository;

    public List<UserLibro> buscarPorUserId(int id){
        return repository.findByUser_Id(id);
    }

    public Page<UserLibro> findByUser_IdAndFechaDevolucionIsNotNull(int id, Pageable paginable){
        return repository.findByUser_IdAndFechaDevolucionIsNotNull(id, paginable);
    }

    public Page<UserLibro> findByUser_IdAndFechaDevolucionIsNull(int id, Date from, Date to, Pageable paginable){
        return repository.findByUser_IdAndFechaDevolucionIsNullAndFechaInicioBetween(id, from, to, paginable);
    }

    public Page<UserLibro> buscarPorUserId(int id, int page, int size){

        Pageable paginable = PageRequest.of(page, size);
        return repository.findByUser_Id(id, paginable);
    }

    public Optional<UserLibro> buscarPorId(int id){
        return repository.findById(id);
    }

    public void empezarPrestamosParaUsuario(User user, Libro libro){
        UserLibro relacion = new UserLibro();
        relacion.setUser(user);
        relacion.setLibro(libro);

        repository.save(relacion);
    }

    public void guardarPrestamo(UserLibro prestamo){
        repository.save(prestamo);
    }

    public Page<UserLibro> buscarPrestamos(int page, int size){
        // Crear objeto Pageable usando numero de pag, tamaño y campo por el que se ordena

        Pageable paginable = PageRequest.of(page, size);
        return repository.findAll(paginable);
    }
}
