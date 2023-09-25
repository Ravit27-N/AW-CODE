package com.tessi.cxm.pfl.ms11.exception;

import java.util.List;
public class DefaultINIConfigNotExistException extends RuntimeException {

  public DefaultINIConfigNotExistException(String message) {
    super(message);
  }

  public DefaultINIConfigNotExistException(List<String> noneExisting) {
    this(String.format("None %s models are configured for the config_postal.ini file.", noneExisting));
  }
}
