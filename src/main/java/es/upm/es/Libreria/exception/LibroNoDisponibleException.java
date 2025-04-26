
package es.upm.es.Libreria.exception;

public class LibroNoDisponibleException extends RuntimeException{
    public LibroNoDisponibleException(Integer id) {
        super("El libro con id " + id + " no está disponible para un prestamo");
    }
}
