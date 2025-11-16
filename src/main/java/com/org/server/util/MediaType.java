package com.org.server.util;

public enum MediaType {

    MEDIA_PNG("image/png"),
    MEDIA_JPG("image/jpg"),
    MEDIA_WHITE_BOARD("application/octet-stream");

    String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
