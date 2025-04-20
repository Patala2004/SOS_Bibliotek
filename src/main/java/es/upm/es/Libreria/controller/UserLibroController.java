package es.upm.es.Libreria.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import es.upm.es.Libreria.assembler.LibroModelAssembler;
import es.upm.es.Libreria.assembler.UserLibroModelAssembler;
import es.upm.es.Libreria.assembler.UserModelAssembler;
import es.upm.es.Libreria.exception.LibroExistsException;
import es.upm.es.Libreria.exception.LibroNotFoundException;
import es.upm.es.Libreria.exception.UserNotFoundException;
import es.upm.es.Libreria.model.*;
import es.upm.es.Libreria.repository.LibroRepository;
import es.upm.es.Libreria.repository.UserLibroRepository;
import es.upm.es.Libreria.repository.UserRepository;
import es.upm.es.Libreria.service.LibroService;
import es.upm.es.Libreria.service.UserLibroService;
import es.upm.es.Libreria.service.UserService;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
@AllArgsConstructor
@JacksonXmlRootElement
public class UserLibroController {
    private final UserLibroService service;

    private final LibroService libroService;
    private final UserService userService;


    private final UserLibroRepository repository;
    private final LibroRepository repositoryLibro;
    private final UserRepository repositoryUser;

    private PagedResourcesAssembler<UserLibro> pagedResourcesAssembler;
    private UserLibroModelAssembler userLibroModelAssembler;

    // hacer prestamo
    @PostMapping
    public ResponseEntity<Void> addLibroToUser(@Valid @RequestBody UserLibroId nuevoUserLibro) {
        User user = userService.buscarPorId(nuevoUserLibro.getUserId())
        .orElseThrow(() -> new UserNotFoundException(nuevoUserLibro.getUserId()));

        Libro libro = libroService.buscarPorId(nuevoUserLibro.getLibroId())
        .orElseThrow(() -> new LibroNotFoundException(nuevoUserLibro.getLibroId()));

        service.empezarPrestamosParaUsuario(user, libro);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "", produces = {"application/json", "application/xml"})
    public ResponseEntity<PagedModel<UserLibro>> getPrestamos(
    @RequestParam(defaultValue="false", required = false) Boolean onlyActive, // filtrar solo prestamos activos
    @RequestParam(defaultValue="0", required = false) int page,
    @RequestParam(defaultValue="2", required = false) int size) {
        Page<UserLibro> prestamos = service.buscarPrestamos(onlyActive, page, size);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(prestamos, userLibroModelAssembler));
    }

    @GetMapping(value = "{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<UserLibro> getPrestamo(@PathVariable Integer id) {
        UserLibro prestamo = service.buscarPorId(id);
        return ResponseEntity.ok(userLibroModelAssembler.toModel(prestamo)); // userLibroModelAssembler adds the links
    }
    
}
