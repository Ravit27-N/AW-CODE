package com.allweb.rms.service.elastic;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RequestType {
  UPDATE("update");

  private final String type;

  @Override
  public String toString() {
    return type;
  }
}
