package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.NOT_FOUND)
public class BusinessUnitNotFoundException extends RuntimeException{
    public BusinessUnitNotFoundException(long id) {
        super("Unable to find the business with id: "+ id + "!");
    }

    public BusinessUnitNotFoundException(String message) {
        super(message);
    }
}
