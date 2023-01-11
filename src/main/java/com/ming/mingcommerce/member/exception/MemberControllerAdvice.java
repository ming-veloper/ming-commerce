package com.ming.mingcommerce.member.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class MemberControllerAdvice {
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResult> memberException(MemberException exception) {
        log.info(exception.toString());
        String message = exception.getMessage();
        ErrorResult result = new ErrorResult(message);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
