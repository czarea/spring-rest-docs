package com.czarea.spring.restdocs.exception;

/**
 * @author zhouzx
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
