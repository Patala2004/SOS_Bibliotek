package es.upm.es.Libreria.assembler;

import es.upm.es.Libreria.controller.LibroController;
import es.upm.es.Libreria.model.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class LibroModelAssembler extends RepresentationModelAssemblerSupport<Libro, Libro>{
    public LibroModelAssembler(){
        super(LibroController.class, Libro.class);
    }

    @Override
    public Libro toModel(Libro entity){
        entity.add(linkTo(methodOn(LibroController.class).getLibro(entity.getId())).withSelfRel());
        return entity;
    }
}
