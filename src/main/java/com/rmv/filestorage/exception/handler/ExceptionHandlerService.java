package com.rmv.filestorage.exception.handler;

import com.rmv.filestorage.dto.ErrorInfo;
import com.rmv.filestorage.exception.BadRequestException;
import com.rmv.filestorage.exception.FileNotFoundInRepositoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerService {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({FileNotFoundInRepositoryException.class, BadRequestException.class})
    @ResponseBody
    public ResponseEntity<ErrorInfo> exceptionHandler(Exception ex) {
        return new ResponseEntity<>(new ErrorInfo(ex.getMessage()),
                ex instanceof FileNotFoundInRepositoryException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST);
    }

}
