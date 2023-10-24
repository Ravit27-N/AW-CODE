package com.innovationandtrust.utils.file.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

@Slf4j
public class ZipCommand {
  private static final String ZIP_EXTENSION = "zip";
  private final int byteSize;
  private final Path sourceDir;
  private final Path targetDir;

  private ZipCommand(Builder builder) {
    this.byteSize = builder.byteSize;
    this.sourceDir = builder.sourceDir;
    this.targetDir = builder.targetDir;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Execute the compress command.
   *
   * @throws IOException if any I/O error occurs
   */
  public void exec() throws IOException {
    // get folder name as zip file name
    String zipFileName = this.sourceDir.toString().concat(".".concat(ZIP_EXTENSION));
    if (this.targetDir != null) {
      zipFileName = this.targetDir.toString();
    }
    final Path source = this.sourceDir;
    try (final ZipOutputStream outputStream =
        new ZipOutputStream(new FileOutputStream(zipFileName))) {
      Files.walkFileTree(
          source,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                throws IOException {
              // only copy files, no symbolic links
              if (attributes.isSymbolicLink()) {
                return FileVisitResult.CONTINUE;
              }
              try (FileInputStream fis = new FileInputStream(file.toFile())) {
                Path targetFile = source.relativize(file);
                outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
                IOUtils.copy(fis, outputStream, byteSize);
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
              log.error("Unable to zip :" + file + " " + exc);
              return FileVisitResult.CONTINUE;
            }
          });
    }
  }

  public static class Builder {
    private Path targetDir;
    private Path sourceDir;
    private int byteSize = 8192;

    private Builder() {}

    /**
     * (REQUIRED) Source filepath to compress.
     *
     * @param sourceDir the filepath to compress
     * @return this
     */
    public Builder sourceDir(Path sourceDir) {
      this.sourceDir = sourceDir;
      return this;
    }

    /**
     * (OPTIONAL) Target directory where the compress files should be placed. The given input has to
     * be an existing directory.
     *
     * <p>Example: Compressing "/source/foo.zip" to target directory "/target/," the results will be
     * found in directory "/target/foo/".
     *
     * @param dir existing target directory
     * @return this
     */
    public Builder targetDir(Path dir) {
      this.targetDir = dir;
      return this;
    }

    /**
     * (OPTIONAL) Byte size for to compress buffer. The value must be positive. Default to 8192
     * bytes.
     *
     * @param byteSize byte size for to compress buffer
     * @return this
     */
    public Builder bufferSize(int byteSize) {
      this.byteSize = byteSize;
      return this;
    }

    public ZipCommand build() {
      Objects.requireNonNull(sourceDir);
      if (this.targetDir != null) {
        if (!FilenameUtils.getExtension(this.targetDir.toString()).equals(ZIP_EXTENSION)) {
          throw new IllegalArgumentException(
              "Invalid resource file extension. Extension file resource must be zip");
        }
        int lastSegment = this.sourceDir.getNameCount() - 1;
        if ((this.targetDir.getNameCount() >= this.sourceDir.getNameCount())
            && (this.sourceDir.getName(0).equals(this.targetDir.getName(0))
                && this.targetDir
                    .getName(lastSegment)
                    .equals(this.sourceDir.getName(lastSegment)))) {
          throw new IllegalArgumentException(
              "Required targetPath must not belong to sourcePath, but targetPath="
                  + this.targetDir.getName(lastSegment)
                  + " and sourcePath="
                  + this.sourceDir.getName(lastSegment));
        }
      }
      if (byteSize <= 0) {
        throw new IllegalArgumentException("Required positive value, but byteSize=" + byteSize);
      }
      return new ZipCommand(this);
    }
  }
}
