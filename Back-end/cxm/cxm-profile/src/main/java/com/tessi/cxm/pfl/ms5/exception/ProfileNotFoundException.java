package com.tessi.cxm.pfl.ms5.exception;

public class ProfileNotFoundException extends RuntimeException {

  public ProfileNotFoundException(Long profileId) {
    super("Profile not found: " + profileId);
  }

}
