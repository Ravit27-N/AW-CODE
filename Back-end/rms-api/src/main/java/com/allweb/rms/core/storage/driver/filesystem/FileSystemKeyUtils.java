package com.allweb.rms.core.storage.driver.filesystem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;

public final class FileSystemKeyUtils {

  private static final String[][] ESCAPES = {
    {"+", "_P"}, {"-", "_M"}, {"/", "_S"}, {".", "_D"}, {"=", "_E"}
  };

  private FileSystemKeyUtils() {}

  public static String encodeHashKey(String key) {
    String base64Encoded = new String(Base64.encodeBase64(key.getBytes()));
    return fixBase64HashString(base64Encoded, EncodingType.ENCODE);
  }

  public static String decodeHashKey(String encodedHashKey) {
    String base64Decoded = new String(Base64.decodeBase64(encodedHashKey));
    return fixBase64HashString(base64Decoded, EncodingType.ENCODE);
  }

  private static String fixBase64HashString(String base64String, EncodingType encodeType) {
    for (String[] escape : ESCAPES) {
      base64String =
          base64String.replace(
              escape[encodeType.getValue()], escape[2 - encodeType.getValue() - 1]);
    }
    return base64String;
  }

  @RequiredArgsConstructor
  enum EncodingType {
    ENCODE(0),
    DECODE(1);

    @Getter private final int value;
  }
}
