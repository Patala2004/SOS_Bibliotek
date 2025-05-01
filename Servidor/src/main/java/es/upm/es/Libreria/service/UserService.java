package es.upm.es.Libreria.service;


import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import es.upm.es.Libreria.repository.UserLibroRepository;
import es.upm.es.Libreria.repository.UserRepository;
import lombok.*;
import es.upm.es.Libreria.model.User;
import es.upm.es.Libreria.model.UserLibro;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    private final UserLibroRepository prestamoRepository;

    public boolean existeUsuario(String nombre){
        return repository.existsByNombre(nombre);
    }

    public boolean existeUsuarioPorId(Integer id){
        return repository.existsById(id);
    }

    public boolean existeUsuarioPorMatricula(Integer matricula){
        return repository.existsByMatricula(matricula);
    }

    public User crearUsuario(User user) {
        return repository.save(user);
    }

    public Optional<User> buscarPorId(int id) {
        Optional<User> user = repository.findById(id);
        if(user.isPresent()){
            List<UserLibro> prestamosQueCausanSancion = prestamoRepository.findByUser_Id(id).stream().filter(prestamo -> prestamo.getFechaDevolucion() != null && prestamo.getFechaDevolucion().compareTo(prestamo.getFechaFin()) > 0 && 
                prestamo.getFechaDevolucion().compareTo(Date.valueOf(LocalDate.now().minusDays(7))) > 0).sorted(Comparator.comparing(UserLibro::getFechaDevolucion).reversed()).toList();

            if(!prestamosQueCausanSancion.isEmpty()){
                user.get().setSancionadoHasta(Date.valueOf(prestamosQueCausanSancion.getFirst().getFechaDevolucion().toLocalDate().plusDays(7)));
            }
        }
        return repository.findById(id);
    }

    public Page<User> buscarUsuarios(String starts_with, int page, int size){
        // Crear objeto Pageable usando numero de pag, tamaño y campo por el que se ordena

        Pageable paginable = PageRequest.of(page, size);
        Page<User> res;
        if(starts_with == null){
            res = repository.findAll(paginable);
            
        }
        else{
            res = repository.findByNombreStartsWith(starts_with, paginable);
        }

        res.forEach(user -> {
            List<UserLibro> prestamosQueCausanSancion = prestamoRepository.findByUser_Id(user.getId()).stream().filter(prestamo -> prestamo.getFechaDevolucion() != null && prestamo.getFechaDevolucion().compareTo(prestamo.getFechaFin()) > 0 && 
            prestamo.getFechaDevolucion().compareTo(Date.valueOf(LocalDate.now().minusDays(7))) > 0).sorted(Comparator.comparing(UserLibro::getFechaDevolucion).reversed()).toList();

            if(!prestamosQueCausanSancion.isEmpty()){
                user.setSancionadoHasta(Date.valueOf(prestamosQueCausanSancion.getFirst().getFechaDevolucion().toLocalDate().plusDays(7)));
            }
        });
        return res;
    }

    public void eliminarUsuario(int id){

        List<UserLibro> prestamos = prestamoRepository.findByUser_Id(id);
        for(UserLibro prestamo : prestamos){
            prestamoRepository.delete(prestamo);
        }
        repository.deleteById(id);
    }
}

