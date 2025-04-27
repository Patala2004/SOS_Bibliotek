package es.upm.es.Libreria.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.PagedModel.PageMetadata;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import es.upm.es.Libreria.exception.*;
import es.upm.es.Libreria.model.*;
import es.upm.es.Libreria.repository.LibroRepository;
import es.upm.es.Libreria.repository.UserLibroRepository;
import es.upm.es.Libreria.repository.UserRepository;
import es.upm.es.Libreria.service.LibroService;
import es.upm.es.Libreria.service.UserLibroService;
import es.upm.es.Libreria.service.UserService;
import jakarta.validation.Valid;

import java.sql.Date;
import java.util.*;

import lombok.AllArgsConstructor;

import es.upm.es.Libreria.assembler.*;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
@JacksonXmlRootElement
public class UserController {
    private final UserService service;

    private final LibroService libroService;
    private final UserLibroService userLibroService;


    private final UserRepository repository;
    private final LibroRepository repositoryLibro;
    private final UserLibroRepository repositoryUserLibro;

    private PagedResourcesAssembler<User> pagedResourcesAssembler;
    private PagedResourcesAssembler<Libro> pagedLibroResourcesAssembler;
    private PagedResourcesAssembler<UserLibro> pagedUserLibroResourcesAssembler;
    private UserModelAssembler userModelAssembler;
    private LibroModelAssembler libroModelAssembler;
    private UserLibroModelAssembler userLibroModelAssembler;


    // POST User
    @PostMapping
    ResponseEntity<Void> newUser(@Valid @RequestBody User newUser){
        // vemos si existe el usuario
        if(service.existeUsuarioPorId(newUser.getId())){
            throw new UserExistsException(newUser.getId());
        }
        User user = service.crearUsuario(newUser);

        return ResponseEntity.created(linkTo(UserController.class).slash(user.getId()).toUri()).build();
    }
    
    // Get user by id
    @GetMapping(value = "{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<User> getUser(@PathVariable Integer id){
        User user = service.buscarPorId(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        List<EntityModel<Libro>> prestamos = new ArrayList<>();
        List<EntityModel<Libro>> historial = new ArrayList<>();

        for(UserLibro prestamo : userLibroService.buscarPorUserId(id).stream().filter(prestamo -> prestamo.getFechaDevolucion() == null).sorted(Comparator.comparing(UserLibro::getFechaInicio).reversed()).toList()){
            prestamos.add(EntityModel.of(prestamo.getLibro(), linkTo(methodOn(LibroController.class).getLibro(prestamo.getLibro().getId())).withSelfRel()));
        }

        int counterOnlyFive = 5;
        for(UserLibro prestamo : userLibroService.buscarPorUserId(id).stream().filter(prestamo -> prestamo.getFechaDevolucion() != null).sorted(Comparator.comparing(UserLibro::getFechaDevolucion).reversed()).toList()){
            if(counterOnlyFive <= 0) break;
            historial.add(EntityModel.of(prestamo.getLibro(), linkTo(methodOn(LibroController.class).getLibro(prestamo.getLibro().getId())).withSelfRel()));
            counterOnlyFive--;
        }
        user.setPrestamos(prestamos);
        user.setHistorial(historial);
        user.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        return ResponseEntity.ok(user);
    }

    // Get user by id and show only basic info
    @GetMapping(value = "{id}/basico", produces = {"application/json", "application/xml"})
    public ResponseEntity<User> getUserBasicInfo(@PathVariable Integer id){
        User user = service.buscarPorId(id)
        .orElseThrow(() -> new UserNotFoundException(id));
        user.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        return ResponseEntity.ok(user);
    }

    //Put
    @PutMapping("/{id}")
    public ResponseEntity<Void> replaceUser(@Valid @RequestBody User newUser, @PathVariable Integer id) {
        service.buscarPorId(id).map(user -> {
            user.setNombre(newUser.getNombre());
            user.setEmail(newUser.getEmail());
            user.setMatricula(newUser.getMatricula());
            user.setFechaNacimiento(newUser.getFechaNacimiento());
            return service.crearUsuario(user);
        }).orElseThrow(() -> new UserNotFoundException(id));

        return ResponseEntity.noContent().build();
    }

    // Delete
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id){
        if(!service.existeUsuarioPorId(id)){
            throw new LibroNotFoundException(id);
        }
        service.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }

    // Get todos los users
    @GetMapping(value = "", produces = {"application/json", "application/xml"})
    public ResponseEntity<PagedModel<User>> getUsers(
    @RequestParam(required = false) String starts_with,
    @RequestParam(defaultValue="0", required = false) int page,
    @RequestParam(defaultValue="2", required = false) int size) {

        Page<User> users = service.buscarUsuarios(starts_with, page, size);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(users, userModelAssembler));
    }

    // // hacer prestamo
    // @PostMapping("/{id}/libros")
    // public ResponseEntity<Void> addLibroToUser(@PathVariable Integer id, @Valid @RequestBody UserLibroId nuevoUserLibro) {
    //     User user = service.buscarPorId(id)
    //     .orElseThrow(() -> new UserNotFoundException(id));

    //     Libro libro = libroService.buscarPorId(nuevoUserLibro.getLibroId())
    //     .orElseThrow(() -> new LibroNotFoundException(nuevoUserLibro.getLibroId()));

    //     userLibroService.empezarPrestamosParaUsuario(user, libro);
    //     return ResponseEntity.noContent().build();
    // }

    // Get histórico de libros que ha tenido prestado un usuario
    @GetMapping(value = "{id}/historico", produces = {"application/json", "application/xml"})
    public ResponseEntity<PagedModel<Libro>> getHistorico(
    @PathVariable Integer id,
    @RequestParam(defaultValue="0", required = false) int page,
    @RequestParam(defaultValue="2", required = false) int size){

        Pageable paginable = PageRequest.of(page, size);

        if(!service.existeUsuarioPorId(id)) throw new UserNotFoundException(id);

        List<Libro> historicoLibros = userLibroService.buscarPorUserId(id).stream()
        .filter(prestamo -> prestamo.getFechaDevolucion() != null)
        .sorted(Comparator.comparing(UserLibro::getFechaDevolucion).reversed())
        .map(UserLibro::getLibro)
        .toList();

        int start = (int) paginable.getOffset();
        int end = Math.min((start + paginable.getPageSize()), historicoLibros.size());
        List<Libro> paginatedList = historicoLibros.subList(start, end);

        Page<Libro> pageLibro = new PageImpl<>(paginatedList, paginable, historicoLibros.size());
        return ResponseEntity.ok(pagedLibroResourcesAssembler.toModel(pageLibro, libroModelAssembler));
    }

    // Get prestamos actuales de libros que ha tenido prestado un usuario
    @GetMapping(value = "{id}/prestamos", produces = {"application/json", "application/xml"})
    public ResponseEntity<PagedModel<UserLibro>> getPrestamos(
    @PathVariable Integer id,
    @RequestParam(defaultValue="0001-01-01", required = false) Date from,
    @RequestParam(defaultValue="9999-12-31", required = false) Date to,
    @RequestParam(defaultValue="0", required = false) int page,
    @RequestParam(defaultValue="2", required = false) int size){

        Pageable paginable = PageRequest.of(page, size);

        if(!service.existeUsuarioPorId(id)) throw new UserNotFoundException(id);

        Page<UserLibro> pagePrestamos = userLibroService.findByUser_IdAndFechaDevolucionIsNull(id,from, to, paginable);

        pagePrestamos.forEach(prestamo -> prestamo.setUser(null)); // Set to null para que no aparezca en el json (ya sabemos cual es el usuario)

        return ResponseEntity.ok(pagedUserLibroResourcesAssembler.toModel(pagePrestamos, userLibroModelAssembler));
    }

}
