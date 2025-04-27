package es.upm.es.Libreria.exception;

public class PrestamoNotFoundException extends RuntimeException{
    public PrestamoNotFoundException(Integer id) {
        super("Prestamo con id " + id + " no encontrado");
    }
}
