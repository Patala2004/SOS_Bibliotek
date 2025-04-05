package es.upm.es.Libreria.service;


import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import es.upm.es.Libreria.repository.UserRepository;
import lombok.*;
import es.upm.es.Libreria.model.User;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

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
        return repository.findById(id);
    }

    public Page<User> buscarUsuarios(String starts_with, int page, int size){
        // Crear objeto Pageable usando numero de pag, tamaño y campo por el que se ordena

        Pageable paginable = PageRequest.of(page, size);
        if(starts_with == null){
            return repository.findAll(paginable);
        }
        else{
            return repository.findByNombreStartsWith(starts_with, paginable);
        }
    }

    public void eliminarUsuario(int id){
        repository.deleteById(id);
    }
}

