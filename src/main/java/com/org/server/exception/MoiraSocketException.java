package com.org.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class MoiraSocketException extends RuntimeException{

    private Long projectId;
    private String requestId;

    public MoiraSocketException(String message,Long projectId,String requestId) {
        super(message);
        this.projectId=projectId;
        this.requestId=requestId;
    }
}
