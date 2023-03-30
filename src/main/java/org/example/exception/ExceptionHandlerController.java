package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(WrongParametersException.class)
    public ResponseEntity<String> handleWrongParametersException() {
        String message = "Wrong parameters were provided";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}

