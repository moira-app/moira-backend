package com.org.server.exception;

import com.org.server.graph.dto.NodeDto;
import lombok.Getter;


@Getter
public class SocketException extends RuntimeException{

    private SocketExceptionType socketExceptionType;
    private Object data;

    public SocketException(String message,SocketExceptionType socketExceptionType,Object data) {
        super(message);
        this.socketExceptionType=socketExceptionType;
        this.data=data;
    }
}
