package com.pasciitools.pithy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Post not found")
public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(String postNotFound) {
        super(postNotFound);
    }
}
