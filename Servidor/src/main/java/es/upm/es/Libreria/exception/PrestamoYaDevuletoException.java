package es.upm.es.Libreria.exception;

public class PrestamoYaDevuletoException extends RuntimeException{
    public PrestamoYaDevuletoException(Integer id) {
        super("El prestamo con id " + id + " ya ha sido finalizado");
    }
}
