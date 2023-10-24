package com.innovationandtrust.utils.file.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

@Slf4j
public class UnzipCommand {
  private final int byteSize;
  private final Path sourceZip;
  private final Path targetDir;

  private UnzipCommand(Builder builder) {
    this.byteSize = builder.byteSize;
    this.sourceZip = builder.sourceZip;
    this.targetDir = builder.targetDir;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Execute the unzip command.
   *
   * @throws IOException if any I/O error occurs
   */
  public void exec() throws IOException {
    try (InputStream in = new FileInputStream(this.sourceZip.toFile());
        ArchiveInputStream i =
            new ZipArchiveInputStream(
                new BufferedInputStream(in), CharEncoding.UTF_8, true, true)) {
      ArchiveEntry entry;
      while ((entry = i.getNextEntry()) != null) {
        if (!i.canReadEntryData(entry)) {
          log.info("Can't read entry: {}", entry);
          continue;
        }
        var destDirectory = zipSlipProtect(entry, this.targetDir).toString();
        File f = new File(destDirectory);
        if (entry.isDirectory()) {
          if (!f.isDirectory() && !f.mkdirs()) {
            throw new IOException("failed to create directory " + f);
          }
        } else {
          File parent = f.getParentFile();
          if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("failed to create directory " + parent);
          }
          try (OutputStream o = new FileOutputStream(f)) {
            IOUtils.copy(i, o, byteSize);
          }
        }
      }
    }
  }

  // protect zip slip attack
  private Path zipSlipProtect(ArchiveEntry zipEntry, Path targetDir) throws IOException {
    Path targetDirResolved = targetDir.resolve(zipEntry.getName());

    // make sure normalized file still has targetDir as its prefix
    // else throws exception
    Path normalizePath = targetDirResolved.normalize();
    if (!normalizePath.startsWith(targetDir)) {
      throw new IOException("Bad zip entry: " + zipEntry.getName());
    }

    return normalizePath;
  }

  public static class Builder {
    private Path targetDir;
    private Path sourceZip;
    private int byteSize = 8192;

    private Builder() {}

    /**
     * (REQUIRED) Source filepath to unzip.
     *
     * @param zip the filepath to unzip
     * @return this
     */
    public Builder sourceZip(Path zip) {
      this.sourceZip = zip;
      return this;
    }

    /**
     * (REQUIRED) Target directory where the unzipped files should be placed. The given input has to
     * be an existing directory.
     *
     * <p>Example: Unzipping "/source/foo.zip" to target directory "/target/," the results will be
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
     * (OPTIONAL) Byte size for to unzip buffer. The value must be positive. Default to 1024 bytes.
     *
     * @param byteSize byte size for to unzip buffer
     * @return this
     */
    public Builder bufferSize(int byteSize) {
      this.byteSize = byteSize;
      return this;
    }

    public UnzipCommand build() {

      Objects.requireNonNull(sourceZip);
      Objects.requireNonNull(targetDir);
      if (byteSize <= 0) {
        throw new IllegalArgumentException("Required positive value, but byteSize=" + byteSize);
      }
      if (FilenameUtils.equals(sourceZip.toString(), targetDir.toString())) {
        targetDir = Path.of(FilenameUtils.getFullPath(targetDir.toString()));
      }
      return new UnzipCommand(this);
    }
  }
}
