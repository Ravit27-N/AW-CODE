package com.innovationandtrust.process.constant;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignatureFileConstant {
  public static final String JPG = "jpg";
  public static final String JPEG = "jpeg";
  public static final String PNG = "png";
  public static final String GIF = "gif";
  public static final String MAX_SIZE = "500KB";
  public static Set<String> getFileExtensions() {
    return Stream.of(JPG, JPEG, PNG, GIF).collect(Collectors.toSet());
  }
}
