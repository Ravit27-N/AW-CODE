package com.tessi.cxm.pfl.ms8.util;

import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.exception.FileNotFoundException;
import com.tessi.cxm.pfl.shared.utils.MultipartFileInstance;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public final class BackgroundFileUtil {

  private BackgroundFileUtil() {
  }

  public static File findFileWithPrefix(Path parenDirectory, String prefix) {
    File dir = parenDirectory.toFile();
    File[] foundFiles = dir.listFiles(
        (dir1, name) -> name.startsWith(prefix));
    if (!Objects.isNull(foundFiles)) {
      return Arrays.stream(foundFiles).findFirst()
          .orElseThrow(() -> new FileNotFoundException("File not found"));
    }

    throw new FileNotFoundException("File not found");
  }

  public static MultipartFile findMultipartFileWithPrefix(Path parenDirectory, String prefix) {
    try{
      return new MultipartFileInstance(findFileWithPrefix(parenDirectory, prefix));
    }catch (Exception e){
      log.error(e.getMessage());
      throw new FileErrorException("Fail to convert file to multipart");
    }
  }

  public static void deleteFileWithPrefix(Path parenDirectory, String prefix) {
    try {
      File dir = parenDirectory.toFile();
      File[] foundFiles = dir.listFiles(
          (dir1, name) -> name.startsWith(prefix));
      if (!Objects.isNull(foundFiles)) {
        for (var file : foundFiles) {
          log.info("<< File is deleting on a path: {} >>.", file.getPath());
          Files.deleteIfExists(Paths.get(file.getPath()));
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
