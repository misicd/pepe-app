package org.dmship.exceptions;

public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String errorMessage) {
        super(errorMessage);
    }
}
