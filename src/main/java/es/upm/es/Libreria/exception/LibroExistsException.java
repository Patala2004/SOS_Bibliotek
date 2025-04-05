package es.upm.es.Libreria.exception;

public class LibroExistsException extends RuntimeException{
    public LibroExistsException(String nombre){
        super("Libro con nombre " + nombre + " ya existe.");
    }
}
