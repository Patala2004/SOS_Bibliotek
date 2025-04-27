package es.upm.es.Libreria.exception;

public class LibroNotFoundException extends RuntimeException{
    public LibroNotFoundException(Integer id) {
        super("Libro con id " + id + " no encontrado");
    }
}
