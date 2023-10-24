package com.innovationandtrust.profile.utils;

import lombok.Getter;

@Getter
public class JsonResponse<T> {
  private final String message;
  private final T data;

  public JsonResponse(String message, T data) {
    this.message = message;
    this.data = data;
  }

}
