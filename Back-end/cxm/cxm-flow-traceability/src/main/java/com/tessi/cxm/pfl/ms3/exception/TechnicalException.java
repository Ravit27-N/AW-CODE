package com.tessi.cxm.pfl.ms3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class TechnicalException extends RuntimeException {

    private static final  long serialVersionUID = 1L;

    public TechnicalException(Exception e) { super(e);}

    public TechnicalException(String message) { super(message);}

    public TechnicalException() { super();}
}
