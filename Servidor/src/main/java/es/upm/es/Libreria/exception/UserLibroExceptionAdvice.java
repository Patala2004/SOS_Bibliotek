
package es.upm.es.Libreria.exception;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class UserLibroExceptionAdvice {
    @ExceptionHandler(PrestamoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorMessage prestamoNotFoundHandler(PrestamoNotFoundException ex) {
        return new ErrorMessage(ex.getMessage());
    }

	@ExceptionHandler(PrestamoYaDevuletoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage prestamoYaDevueltoHandler(PrestamoYaDevuletoException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(PrestamoAmpliationNuevaFechaInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage PrestamoAmpliationNuevaFechaInvalidExceptionHandler(PrestamoAmpliationNuevaFechaInvalidException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(PrestamoAmpliationPlazoFinalizadoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage PrestamoAmpliationPlazoFinalizadoExceptionHandler(PrestamoAmpliationPlazoFinalizadoException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ErrorMessage(errors.toString());
    }


}
