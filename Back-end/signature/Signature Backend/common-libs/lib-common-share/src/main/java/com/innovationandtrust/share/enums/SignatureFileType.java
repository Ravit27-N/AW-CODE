package com.innovationandtrust.share.enums;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/** Signature setting level. */
@Getter
public enum SignatureFileType {
  PDF(1, "PDF"),
  JPG(2, "JPG"),
  PNG(3, "PNG");

  private final Integer order;
  @Getter private final String value;

  SignatureFileType(int order, String value) {
    this.order = order;
    this.value = value;
  }

  public static Set<String> getFileTypes() {
    return Stream.of(PDF.value, JPG.value, PNG.value).collect(Collectors.toSet());
  }
}
