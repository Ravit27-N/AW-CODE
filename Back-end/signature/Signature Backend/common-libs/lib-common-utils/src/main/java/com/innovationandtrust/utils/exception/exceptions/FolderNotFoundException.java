package com.innovationandtrust.utils.exception.exceptions;

import com.innovationandtrust.utils.exception.config.HandledException;
import org.springframework.http.HttpStatus;

@HandledException(status = HttpStatus.NOT_FOUND, message = "Folder is not found ")
public class FolderNotFoundException extends RuntimeException{

    public FolderNotFoundException() {}

    public FolderNotFoundException(long id) {
        super("Unable to find the folder with id: "+ id + "!");
    }

    public FolderNotFoundException(String message) {
        super(message);
    }
}
