package es.upm.es.Libreria.controller;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
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
    private UserModelAssembler userModelAssembler;


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

        Set<EntityModel<UserLibroDTO>> prestamos = new HashSet<>();
        for(UserLibro userLibroEntity : userLibroService.buscarPorUserId(id)){
            UserLibroDTO userLibroDTO = new UserLibroDTO(userLibroEntity.getId(), userLibroEntity.getLibro());
            userLibroDTO.getLibro().add(linkTo(methodOn(LibroController.class).getLibro(userLibroEntity.getLibro().getId())).withSelfRel());
            prestamos.add(EntityModel.of(userLibroDTO, linkTo(methodOn(UserLibroController.class).getPrestamo(id)).withSelfRel()));
        }


        user.setPrestamos(prestamos);
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
    @RequestParam(defaultValue="", required = false) String starts_with,
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
}
