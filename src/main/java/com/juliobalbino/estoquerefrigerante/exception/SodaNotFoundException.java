package com.juliobalbino.estoquerefrigerante.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SodaNotFoundException extends Exception {

    public SodaNotFoundException(String SodaName) {
        super(String.format("Soda with name %s not found in the system.", SodaName));
    }

    public SodaNotFoundException(Long id) {
        super(String.format("Soda with id %d not found in the system.", id));
    }
}
