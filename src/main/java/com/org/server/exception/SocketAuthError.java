package com.org.server.exception;


import lombok.Getter;

@Getter
public class SocketAuthError extends RuntimeException {
    private String message;

    public SocketAuthError(String message) {
        this.message = message;
    }
}
