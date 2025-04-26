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

    public boolean existeLibro(String titulo){
        return repository.existsByTitulo(titulo);
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

    public Page<Libro> buscarLibros(String starts_with, int page, int size, Boolean disponible){
        // Crear objeto Pageable usando numero de pag, tamaño y campo por el que se ordena

        Pageable paginable = PageRequest.of(page, size);

        if(starts_with == null && disponible == null){ // Si no hay filtros validos
            return repository.findAll(paginable);
        }
        else if(disponible == null){
            return repository.findByTituloStartsWith(starts_with, paginable);
        }
        else if(starts_with == null){
            List<Libro> filteredList = repository.findAll(paginable).stream().filter(libro -> libro.isDisponible() == disponible).toList();
            return new PageImpl<>(filteredList, paginable, filteredList.size());
        }
        else{ // None are null
            List<Libro> filteredList = repository.findByTituloStartsWith(starts_with, paginable).stream().filter(libro -> libro.isDisponible() == disponible).toList();
            return new PageImpl<>(filteredList, paginable, filteredList.size());
        }
    }

    public void eliminarLibro(int id){
        repository.deleteById(id);
    }
}
