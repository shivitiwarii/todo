package com.assignment.todo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String r, String field, Object val) {
        super(String.format("%s not found with %s : '%s'", r, field, val));
    }
}
