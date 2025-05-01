package es.upm.es.Libreria.service;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import es.upm.es.Libreria.repository.LibroRepository;
import es.upm.es.Libreria.repository.UserLibroRepository;
import lombok.*;
import es.upm.es.Libreria.model.*;

@Service
@AllArgsConstructor
public class LibroService {
    private final LibroRepository repository;

    private final UserLibroRepository prestamoRepository;


    public boolean existeLibroPorId(Integer id){
        return repository.existsById(id);
    }

    public Libro crearLibro(Libro libro) {
        return repository.save(libro);
    }

    public Optional<Libro> buscarPorId(int id) {
        return repository.findById(id);
    }

    public Page<Libro> buscarLibros(String starts_with, String contains, int page, int size, Boolean disponible){
        // Crear objeto Pageable usando numero de pag, tamaño y campo por el que se ordena

        Pageable paginable = PageRequest.of(page, size);

        // if(starts_with == null && disponible == null){ // Si no hay filtros validos
        //     return repository.findAll(paginable);
        // }
        // else if(disponible == null){
        //     return repository.findByTituloStartsWith(starts_with, paginable);
        // }
        // else if(starts_with == null){
        //     return repository.findByDisponible(disponible, paginable);
        // }
        // else{ // None are null
        //     return repository.findByTituloStartsWithAndDisponible(starts_with, disponible, paginable);
        // }
        return repository.searchLibros(starts_with+"%", "%"+contains+"%", disponible, paginable);
    }

    public void eliminarLibro(int id){
        List<UserLibro> prestamos = prestamoRepository.findByLibro_Id(id);
        for(UserLibro prestamo : prestamos){
            prestamoRepository.delete(prestamo);
        }
        repository.deleteById(id);
    }
}
