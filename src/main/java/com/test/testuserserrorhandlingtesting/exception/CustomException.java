package com.test.testuserserrorhandlingtesting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class AgeAboveAcceptionalException extends Exception{

    public AgeAboveAcceptionalException(String message) {
        super(message);
    }
}
