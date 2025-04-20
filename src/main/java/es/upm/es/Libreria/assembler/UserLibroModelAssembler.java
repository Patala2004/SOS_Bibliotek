package es.upm.es.Libreria.assembler;


import es.upm.es.Libreria.controller.LibroController;
import es.upm.es.Libreria.controller.UserController;
import es.upm.es.Libreria.controller.UserLibroController;
import es.upm.es.Libreria.model.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserLibroModelAssembler extends RepresentationModelAssemblerSupport<UserLibro, UserLibro>{
    public UserLibroModelAssembler(){
        super(UserLibroController.class, UserLibro.class);
    }

    @Override
    public UserLibro toModel(UserLibro entity){
        entity.add(linkTo(methodOn(UserLibroController.class).getPrestamo(entity.getId())).withSelfRel());
        if (entity.getLibro().getLinks().isEmpty()) {
            entity.getLibro().add(linkTo(methodOn(LibroController.class).getLibro(entity.getLibro().getId())).withSelfRel());
        }
    
        if (entity.getUser().getLinks().isEmpty()) {
            entity.getUser().add(linkTo(methodOn(UserController.class).getUser(entity.getUser().getId())).withSelfRel());
        }
        return entity;
    }
}
