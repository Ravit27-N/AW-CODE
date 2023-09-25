package com.allweb.rms.core.storage.utils;

import static com.allweb.rms.core.storage.StorageConstants.MIME_TYPE_DIRECTORY;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

public class MimeTypeUtils {
  private static final Tika TIKA_INSTANCE = new Tika();

  private MimeTypeUtils() {}

  public static String getMimeType(Path path) throws IOException {
    String mimeType;
    if (Files.exists(path)) {
      if (Files.isDirectory(path)) {
        mimeType = MIME_TYPE_DIRECTORY;
      } else {
        mimeType = TIKA_INSTANCE.detect(path);
      }
    } else {
      mimeType = guessContentTypeFromName(path);
    }
    return mimeType;
  }

  private static String guessContentTypeFromName(Path path) {
    String name = path.getFileName().toString();
    String mimeType = URLConnection.guessContentTypeFromName(name);
    if (StringUtils.isBlank(mimeType) && !path.getFileName().toString().contains(".")) {
      mimeType = MIME_TYPE_DIRECTORY;
    }
    return mimeType;
  }
}
