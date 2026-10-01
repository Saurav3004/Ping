package com.sauravjha.chathub.api;

import com.sauravjha.chathub.api.dto.ApiError;
import com.sauravjha.chathub.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> notFoundExceptionHandler(NotFoundException e, HttpServletRequest request){

    }
}
