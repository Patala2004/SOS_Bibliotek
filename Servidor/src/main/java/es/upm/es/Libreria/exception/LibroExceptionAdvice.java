package es.upm.es.Libreria.exception;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class LibroExceptionAdvice {
    @ExceptionHandler(LibroNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorMessage libroNotFoundHandler(LibroNotFoundException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(LibroExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorMessage libroNotFoundHandler(LibroExistsException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(LibroNoDisponibleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorMessage LibroNoDisponibleHandler(LibroNoDisponibleException ex) {
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
