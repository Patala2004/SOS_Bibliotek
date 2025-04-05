package es.upm.es.Libreria.service;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import es.upm.es.Libreria.repository.LibroRepository;
import lombok.*;
import es.upm.es.Libreria.model.*;

@Service
@AllArgsConstructor
public class LibroService {
    private final LibroRepository repository;

    public boolean existeLibro(String nombre){
        return repository.existsByNombre(nombre);
    }

    public boolean existeLibroPorId(Integer id){
        return repository.existsById(id);
    }

    public Libro crearLibro(Libro libro) {
        return repository.save(libro);
    }

    public Optional<Libro> buscarPorId(int id) {
        return repository.findById(id);
    }

    public Page<Libro> buscarLibros(String starts_with, int page, int size){
        // Crear objeto Pageable usando numero de pag, tamaño y campo por el que se ordena

        Pageable paginable = PageRequest.of(page, size);
        if(starts_with == null){
            return repository.findAll(paginable);
        }
        else{
            return repository.findByNombreStartsWith(starts_with, paginable);
        }
    }

    public void eliminarLibro(int id){
        repository.deleteById(id);
    }
}
