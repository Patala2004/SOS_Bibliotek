package es.upm.es.Libreria.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Integer id) {
        super("Usuario con id " + id + " no encontrado");
    }
}
