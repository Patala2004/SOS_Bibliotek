package es.upm.es.Libreria.exception;

public class PrestamoAmpliationPlazoFinalizadoException extends RuntimeException{
    public PrestamoAmpliationPlazoFinalizadoException(Integer id) {
        super("El prestamo con id " + id + " no puede ser ampliado porque ya ha terminado el plazo.");
    }
}
