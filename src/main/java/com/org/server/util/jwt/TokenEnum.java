package com.org.server.util.jwt;

public enum TokenEnum {
    ACCESS("access"),REFRESH("refresh"),TOKEN_PREFIX("Bearer ");

    String value;

    TokenEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
