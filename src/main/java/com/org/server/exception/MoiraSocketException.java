package com.org.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class MoiraSocketException extends RuntimeException{

    private Long projectId;
    private String rootId;
    private String requestId;

    public MoiraSocketException(String message,Long projectId,String requestId,String rootId) {
        super(message);
        this.projectId=projectId;
        this.requestId=requestId;
        this.rootId=rootId;
    }
}
