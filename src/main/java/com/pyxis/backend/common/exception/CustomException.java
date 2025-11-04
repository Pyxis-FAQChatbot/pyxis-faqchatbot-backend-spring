package com.pyxis.backend.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorType type;
    private Object details;


    public CustomException(ErrorType type) {
        super(type.getDescription());
        this.type = type;
    }

    public CustomException(ErrorType type, Object details) {
        super(type.getDescription());
        this.type = type;
        this.details = details;
    }


}
