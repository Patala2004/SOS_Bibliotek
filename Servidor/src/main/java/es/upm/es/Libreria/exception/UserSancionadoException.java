package es.upm.es.Libreria.exception;


public class UserSancionadoException extends RuntimeException{
    public UserSancionadoException(Integer id) {
        super("Usuario con id " + id + " esta sancionado y no puede hacer prestamos");
    }
}
