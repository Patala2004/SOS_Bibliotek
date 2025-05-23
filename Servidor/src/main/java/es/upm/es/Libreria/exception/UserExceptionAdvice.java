package es.upm.es.Libreria.exception;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class UserExceptionAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorMessage userNotFoundHandler(UserNotFoundException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler(UserSancionadoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage UserSancionadoException(UserSancionadoException ex) {
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
