package com.juliobalbino.estoquerefrigerante.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SodaStockExceededException extends Exception {

    public SodaStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Soda with %d ID to increment informed exceeds the max stock capacity: %d", id, quantityToIncrement));
    }
}
