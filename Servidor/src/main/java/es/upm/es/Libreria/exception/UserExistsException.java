package es.upm.es.Libreria.exception;

public class UserExistsException extends RuntimeException{
    public UserExistsException(Integer matricula){
        super("Usuario con matricula " + matricula + " ya existe.");
    }
}
