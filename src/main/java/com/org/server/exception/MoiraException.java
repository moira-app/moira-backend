package com.org.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MoiraException extends RuntimeException{


    private HttpStatus httpStatus;

    public MoiraException(String message,HttpStatus httpStatus) {
        super(message);
        this.httpStatus=httpStatus;
    }
}
