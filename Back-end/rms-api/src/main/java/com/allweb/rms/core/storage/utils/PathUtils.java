package com.allweb.rms.core.storage.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;

/**
 * Provide some useful utility methods for working with {@link Path}.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 */
public final class PathUtils {

  private static final String[][] ESCAPES = {{"+", "-"}, {"/", "_"}, {"=", "."}};

  private PathUtils() {}

  public static Path removeParentPath(Path parent, String child) { // upload, upload/temp/file.txt
    return removeParentPath(parent, Paths.get(child));
  }

  /**
   * Resolve a child path which is prefixed with a redundant parent's path.
   *
   * <p>If a parent path is "a/b/c" and provided child path is "a/b/c/d" then the result is
   * "a/b/c/d" not "a/b/c/a/b/c/d" which is from joining the parent and child path.
   *
   * @param parent
   * @param child
   * @return
   */
  public static Path removeParentPath(Path parent, Path child) {
    if (child.startsWith(parent)) {
      if (parent.equals(child)) {
        return parent;
      } else {
        int subPathBeginIndex = parent.getNameCount();
        int subPathEndIndex = child.getNameCount();
        return child.subpath(subPathBeginIndex, subPathEndIndex);
      }
    }
    return parent;
  }

  /**
   * Decode a Base64 encoded hash value to a specific {@link Path}.
   *
   * @param hashKey The encoded base64 unique hash value represent a specific path.
   * @return {@link Path}
   */
  public static Path decodeHashKeyAsPath(String hashKey) {
    String pathString = fixBase64HashString(hashKey, EncodingType.DECODE);
    String base64Encoded = new String(Base64.decodeBase64(pathString.getBytes()));
    return Paths.get(base64Encoded);
  }

  /**
   * Decode a Base64 encoded hash value to a specific path as string.
   *
   * @param hashKey The encoded base64 unique hash value represent a specific
   * @return {@link Path}
   */
  public static String decodeHashKeyAsString(String hashKey) {
    String pathString = fixBase64HashString(hashKey, EncodingType.DECODE);
    return new String(Base64.decodeBase64(pathString.getBytes()));
  }

  /**
   * Encode a specific path to base64 format with replacing the "+", "-", "/", "=" and "." to "_P",
   * "_M", "_S", "_E" and "_D".
   *
   * @param path The specific {@link Path} for encoding.
   * @return Encoded base64 string with symbols replaced.
   */
  public static String encodeHashKey(Path path) {
    String base64Encoded = new String(Base64.encodeBase64(path.toString().getBytes()));
    return fixBase64HashString(base64Encoded, EncodingType.ENCODE);
  }

  private static String fixBase64HashString(String base64String, EncodingType encodeType) {
    for (String[] escape : ESCAPES) {
      base64String =
          base64String.replace(
              escape[encodeType.getValue()], escape[2 - encodeType.getValue() - 1]);
    }
    base64String = base64String.replaceAll("\\.+$", "");
    return base64String;
  }

  @RequiredArgsConstructor
  enum EncodingType {
    ENCODE(0),
    DECODE(1);

    @Getter private final int value;
  }
}
