package com.example.internship.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OptimisticLockConflictException extends RuntimeException{
    public OptimisticLockConflictException(String message){
        super(message);
    }
}
