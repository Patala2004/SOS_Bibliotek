package es.upm.es.Libreria.exception;

import java.sql.Date;

public class PrestamoAmpliationNuevaFechaInvalidException extends RuntimeException{
    public PrestamoAmpliationNuevaFechaInvalidException(Date nuevaFecha, Date fechaAnterior) {
        super("Se ha intentado cambiar la fecha de " + fechaAnterior.toString() + " a " + nuevaFecha.toString() + ". La nueva fecha tiene que ser posterior a la anterior");
    }
}

