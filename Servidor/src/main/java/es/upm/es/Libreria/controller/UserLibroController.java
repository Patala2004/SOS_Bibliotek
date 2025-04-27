package es.upm.es.Libreria.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import es.upm.es.Libreria.assembler.LibroModelAssembler;
import es.upm.es.Libreria.assembler.UserLibroModelAssembler;
import es.upm.es.Libreria.assembler.UserModelAssembler;
import es.upm.es.Libreria.exception.LibroExistsException;
import es.upm.es.Libreria.exception.LibroNoDisponibleException;
import es.upm.es.Libreria.exception.LibroNotFoundException;
import es.upm.es.Libreria.exception.PrestamoAmpliationNuevaFechaInvalidException;
import es.upm.es.Libreria.exception.PrestamoAmpliationPlazoFinalizadoException;
import es.upm.es.Libreria.exception.PrestamoNotFoundException;
import es.upm.es.Libreria.exception.PrestamoYaDevuletoException;
import es.upm.es.Libreria.exception.UserNotFoundException;
import es.upm.es.Libreria.exception.UserSancionadoException;
import es.upm.es.Libreria.model.*;
import es.upm.es.Libreria.repository.LibroRepository;
import es.upm.es.Libreria.repository.UserLibroRepository;
import es.upm.es.Libreria.repository.UserRepository;
import es.upm.es.Libreria.service.LibroService;
import es.upm.es.Libreria.service.UserLibroService;
import es.upm.es.Libreria.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.http.HttpResponse;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Void> crearPrestamo(@Valid @RequestBody UserLibroId nuevoUserLibro) {
        User user = userService.buscarPorId(nuevoUserLibro.getUserId())
            .orElseThrow(() -> new UserNotFoundException(nuevoUserLibro.getUserId()));

        Libro libro = libroService.buscarPorId(nuevoUserLibro.getLibroId())
            .orElseThrow(() -> new LibroNotFoundException(nuevoUserLibro.getLibroId()));

        if(!libro.isDisponible()){
            throw new LibroNoDisponibleException(libro.getId());
        }

        if(user.getSancionadoHasta() != null){
            throw new UserSancionadoException(libro.getId());
        }

        libro.setDisponible(false); // Ya no esta disponible
        libroService.crearLibro(libro); // Guardar el no dispnible en el libro

        service.empezarPrestamosParaUsuario(user, libro);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "", produces = {"application/json", "application/xml"})
    public ResponseEntity<PagedModel<UserLibro>> getPrestamos(
    //@RequestParam(defaultValue="false", required = false) Boolean onlyActive, // filtrar solo prestamos activos
    @RequestParam(defaultValue="0", required = false) int page,
    @RequestParam(defaultValue="2", required = false) int size) {
        Page<UserLibro> prestamos = service.buscarPrestamos(page, size);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(prestamos, userLibroModelAssembler));
    }

    @GetMapping(value = "{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<UserLibro> getPrestamo(@PathVariable Integer id) {
        UserLibro prestamo = service.buscarPorId(id)
            .orElseThrow(() -> new PrestamoNotFoundException(id));
        return ResponseEntity.ok(userLibroModelAssembler.toModel(prestamo)); // userLibroModelAssembler adds the links
    }

    @PostMapping(value = "{id}/devolver", produces = {"applictaion/json", "application/xml"})
    public ResponseEntity<String> devolverLibro(@PathVariable Integer id){
        UserLibro prestamo = service.buscarPorId(id)
            .orElseThrow(() -> new PrestamoNotFoundException(id));;

        if(prestamo.getFechaDevolucion() != null){
            throw new PrestamoYaDevuletoException(id);
        }

        Date today = Date.valueOf(LocalDate.now());

        String result;

        prestamo.setFechaDevolucion(today);

        if(prestamo.getFechaFin().compareTo(today) < 0){
            // poner logica de que se ha devuelto muy tarde
            result = "el libro no se ha devuelto a tiempo. Se ha aplicado una sanción de una semana";
        }
        else{
            result = "el libro se ha devuelto a tiempo. Fecha: " + prestamo.getFechaDevolucion();
        }

        

        if(prestamo.getFechaDevolucion() == null){
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

        service.guardarPrestamo(prestamo);

        // Poner libro como disponible otra vez
        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroService.crearLibro(libro); // Guardar cambios del libro

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(value = "{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<String> ampliarPrestamo(@PathVariable Integer id, @Valid @RequestBody UserLibro newPrestamo){
        UserLibro prestamo = service.buscarPorId(id)
            .orElseThrow(() -> new PrestamoNotFoundException(id));;

        if( prestamo.getFechaDevolucion() != null){
            throw new PrestamoYaDevuletoException(id);
        }

        if(prestamo.getFechaFin().compareTo(Date.valueOf(LocalDate.now())) < 0){
            throw new PrestamoAmpliationPlazoFinalizadoException(id);
        }        

        if(prestamo.getFechaFin().compareTo(newPrestamo.getFechaFin()) >= 0){
            throw new PrestamoAmpliationNuevaFechaInvalidException(newPrestamo.getFechaFin(), prestamo.getFechaFin());
        }

        prestamo.setFechaFin(newPrestamo.getFechaFin());
        
        service.guardarPrestamo(prestamo);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "{id}/testingURLToTestSanctionsInClient", produces = {"application/json", "application/xml"})
    @Hidden
    public ResponseEntity<String> cambiarPrestamoTestSecretoSecretisimo(@PathVariable Integer id, @Valid @RequestBody UserLibro newPrestamo){
        UserLibro prestamo = service.buscarPorId(id)
            .orElseThrow(() -> new PrestamoNotFoundException(id));;

        prestamo.setFechaFin(newPrestamo.getFechaFin());
        if(newPrestamo.getFechaDevolucion() != null){
            prestamo.setFechaDevolucion(newPrestamo.getFechaDevolucion());
        }
        
        service.guardarPrestamo(prestamo);

        return ResponseEntity.noContent().build();
    }
    
}
