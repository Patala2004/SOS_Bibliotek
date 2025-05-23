package es.upm.es.Libreria.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import es.upm.es.Libreria.assembler.LibroModelAssembler;
import es.upm.es.Libreria.exception.LibroExistsException;
import es.upm.es.Libreria.exception.LibroNotFoundException;
import es.upm.es.Libreria.model.*;
import es.upm.es.Libreria.repository.LibroRepository;
import es.upm.es.Libreria.service.LibroService;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
@AllArgsConstructor
@JacksonXmlRootElement
public class LibroController {

    private final LibroService service;


    private final LibroRepository repository;

    private PagedResourcesAssembler<Libro> pagedResourcesAssembler;
    private LibroModelAssembler libroModelAssembler;
    
    // POST LIBRO
    @PostMapping
    ResponseEntity<Void> nuevoLibro(@Valid @RequestBody Libro nuevoLibro){

        // Libro puede tener mismo todo si hay varias copias del mismo libro
        
        nuevoLibro.setDisponible(true);

        Libro libro = service.crearLibro(nuevoLibro);

        return ResponseEntity.created(linkTo(LibroController.class).slash(libro.getId()).toUri()).build();
    }

    // Get libro
    // value para añdadir /{id} al path /libros
    // @GetMapping(value = "{id}", produces = {"application/json", "application/xml"})
    // public Libro getLibro(@PathVariable Integer id){
    //     Libro libro = service.buscarPorId(id)
    //     .orElseThrow(() -> new LibroNotFoundException(id));
        
    //     return libro;
    // }


    @GetMapping(value = "{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<Libro> getLibro(@PathVariable Integer id){
        Libro libro = service.buscarPorId(id)
        .orElseThrow(() -> new LibroNotFoundException(id));


        libro.add(linkTo(methodOn(LibroController.class).getLibro(id)).withSelfRel());
        return ResponseEntity.ok(libro);
    }

    // Get todos los libros
    @GetMapping(value = "", produces = {"application/json", "application/xml"})
    public ResponseEntity<PagedModel<Libro>> getLibros(
    @RequestParam(defaultValue="", required = false) String starts_with,
    @RequestParam(defaultValue="", required = false) String contains,
    @RequestParam(required = false) Boolean disponible,
    @RequestParam(defaultValue="0", required = false) int page,
    @RequestParam(defaultValue="2", required = false) int size) {

        Page<Libro> libros = service.buscarLibros(starts_with, contains, page, size, disponible);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(libros, libroModelAssembler));
    }

    // Put libro/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> replaceLibro(@Valid @RequestBody LibroPutDTO newLibro, @PathVariable Integer id) {
        service.buscarPorId(id).map(libro -> {
            if(newLibro.getTitulo() != null) libro.setTitulo(newLibro.getTitulo());
            if(newLibro.getAutores() != null) libro.setAutores(newLibro.getAutores());
            if(newLibro.getEdicion() != null) libro.setEdicion(newLibro.getEdicion());
            if(newLibro.getISBN() != null) libro.setISBN(newLibro.getISBN());
            if(newLibro.getEditorial() != null) libro.setEditorial(newLibro.getEditorial());
            return service.crearLibro(libro);
        }).orElseThrow(() -> new LibroNotFoundException(id));

        return ResponseEntity.noContent().build();
    }

    // Delete
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLibro(@PathVariable Integer id){
        if(!service.existeLibroPorId(id)){
            throw new LibroNotFoundException(id);
        }
        service.eliminarLibro(id);

        return ResponseEntity.noContent().build();
    }
}
