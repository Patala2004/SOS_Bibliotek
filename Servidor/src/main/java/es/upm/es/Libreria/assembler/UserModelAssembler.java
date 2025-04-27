package es.upm.es.Libreria.assembler;

import es.upm.es.Libreria.controller.UserController;
import es.upm.es.Libreria.model.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.sql.Date;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, User>{
    public UserModelAssembler(){
        super(UserController.class, User.class);
    }

    @Override
    public User toModel(User entity){
        entity.add(linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel());
        return entity;
    }
}
