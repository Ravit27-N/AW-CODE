package com.allweb.rms.core.storage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class StorageConstants {
  /** File type. */
  public static final String MIME_TYPE_FILE = "file";
  /** Directory type. */
  public static final String MIME_TYPE_DIRECTORY = "directory";
  /** */
  public static final String ENCODING_SCHEMA = "schema";
  /** */
  public static final String ENCODING_UTF8 = "utf-8";
  /** */
  public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

  private StorageConstants() {}

  /** Construct describe the response fields. */
  @RequiredArgsConstructor
  public enum Fields {

    /** Name of file or directory. */
    NAME("name"),

    /**
     * Hash of current file/dir path, first symbol must be letter, symbols before _underline_ -
     * volume id
     */
    HASH("hash"),

    /**
     * Mime type.
     *
     * <p>Possible values: {@link StorageConstants#MIME_TYPE_FILE}, {@link
     * StorageConstants#MIME_TYPE_DIRECTORY}.
     */
    MIME("mime"),

    /** File modification time in unix timestamp. */
    TIMESTAMP("ts"),

    /** Array of files that were successfully uploaded. */
    ADDED("added"),

    /** */
    REMOVED("removed"),

    /** */
    VOLUME_ID("volumeid"),

    /** */
    PARENT_HASH("phash"),

    /** */
    DIRECTORY_HAS_CHILD("dirs"),

    /** */
    SIZE("size"),

    /** */
    SIZES("sizes"),

    /** */
    UNKNOWN("unknown"),

    /** */
    RENAMED("renamed"),

    /** */
    LIST("list"),

    /** */
    TREE("tree"),

    /** */
    DIRECTORY_COUNT("dirCnt"),

    /** */
    FILE_COUNT("fileCnt"),

    /** */
    ENCODING("encoding"),

    /** */
    CONTENT("content"),

    /** */
    CHANGED("changed"),

    /** */
    FILES("files"),

    /** */
    CURRENT_WORKING_DIRECTORY("cwd"),

    /** */
    READABLE("read"),

    /** */
    WRITABLE("write"),

    /** */
    IS_LOCKED("locked"),

    /**
     * The version number of the protocol, must be >= 2.1.
     *
     * <p>Only for init request!
     */
    API_VERSION("api"),

    /** Not supported yet but required by Elfinder. */
    NET_DRIVERS("netDrivers"),

    OPTIONS("options"),

    /** Current folder path */
    PATH("path"),

    /** */
    SEPARATOR("separator"),

    /** */
    DISABLED("disabled"),

    /** */
    COPY_OVER_WRITE("copyOverwrite"),

    /** */
    UPLOAD_MAP_CONNECTION("uploadMaxConn"),

    /** */
    ARCHIVERS("archivers"),

    /** */
    URL("url"),

    /** */
    RESOURCE_FILE_URI("resourceFileUri"),

    /** */
    DIMENSION("dim");

    private final String field;

    @Override
    public String toString() {
      return field;
    }
  }

  /** Constants describe the req parameters. */
  @RequiredArgsConstructor
  public enum Parameters {

    /** Represent the command name to be executed. */
    COMMAND("cmd"),

    /** Hash of the file or directory. */
    TARGET("target"),

    TARGETS("targets[]"),

    /** Files to be uploaded. */
    UPLOAD_FILES("upload[]"),

    /** The name of file or directory. */
    NAME("name"),

    /** Represent an array of the item names for presence check. */
    INTERSECT("intersect[]"),

    /** */
    DOWNLOAD("download"),

    /** */
    CONTENT("content"),

    /** */
    ENCODING("encoding"),

    /** */
    SEARCH_STRING("q"),

    /** */
    MIMES("mimes"),

    /** */
    DESTINATION("dst"),

    /** */
    IS_CUT("cut"),

    /** */
    RENAMES("renames[]"),

    /** */
    SUFFIX("suffix"),

    /** */
    INIT("init"),

    /** */
    TREE("tree");

    private final String parameter;

    @Override
    public String toString() {
      return parameter;
    }
  }

  /** Constants describe the errors. */
  @RequiredArgsConstructor
  public enum Errors {

    /**
     * The error key represent the errors return by each specific command.
     *
     * <p>Error value type is json array. See below example: {"error" : ["errConf", "errJSON"]}
     */
    KEY(
        "error",
        "The error key represent the errors return bay each specific command.Error value type is array."),

    /** An error occurred when a specific error is unknown. */
    UNKNOWN("errUnknown", "Unknown error."),

    /** An error occurred when a given command is unknown. */
    UNKNOWN_COMMAND("errUnknownCmd", "Unknown command."),

    /** An error occurred when command's parameters is invalid. */
    INVALID_COMMAND_PARAMS("errCmdParams", "Invalid command parameters: {0}."),

    /** An error occurred when a target directory not found. */
    TARGET_DIRECTORY_NOT_FOUND("errTrgFolderNotFound", "Target directory not found."),

    /** An error occurred when a specific file or directory cannot be opened. */
    OPEN("errOpen", "Unable to open {0}"),

    /** An error occurred when a specific object is not a directory. */
    NOT_DIRECTORY("errNotFolder", "Object is not a directory."),

    /** An error occurred when a specific object is not a file. */
    NOT_FILE("errNotFile", "Object is not a file."),

    /** An error occurred when a specific object cannot be read. */
    READ("errRead", "Unable to read {0}."),

    /** An error occurred when a specific file cannot be write. */
    WRITE("errWrite", "Unable to write into {0}."),

    /** An error occurred when a specific object is already exists. */
    ITEM_EXISTS("errExists", "Item named \"{0}\" already exists."),

    /** An error occurred when a specific file name is invalid. */
    INVALID_FILE_NAME("errInvName", "Invalid file name."),

    /** An error occurred when a specific directory name is invalid. */
    INVALID_DIRECTORY_NAME("errInvDirname", "Invalid directory name."),

    /** An error occurred when a specific file is not found. */
    FILE_NOT_FOUND("errFileNotFound", "File not found."),

    /** An error occurred when a specific directory is not found. */
    DIRECTORY_NOT_FOUND("errFolderNotFound", "Directory not found."),

    /** An error occurred when a specific directory is not found. */
    TARGET_FOLDER_NOT_FOUND("errTrgFolderNotFound", "Target folder \"{0}\" not found."),

    /** An error occurred when a file cannot be created. */
    MAKE_FILE("errMkfile", "Unable to create file {0}."),

    /** An error occurred when a directory cannot be created. */
    MAKE_DIRECTORY("errMkdir", "Unable to create directory {0}."),

    /** An error occurred when a specific file or directory cannot be renamed. */
    RENAME("errRename", "Unable to rename \"{0}\"."),

    /** An error occurred when copying a file from a specific volume is not allowed. */
    COPY_FROM("errCopyFrom", "Copying files from volume \"{0}\" not allowed."),

    /** An error occurred when copying a file to a specific volume is not allowed. */
    COPY_TO("errCopyTo", "Copying files to volume \"{0}\" not allowed."),

    /** An error occurred when an uploading is failed. */
    UPLOAD("errUpload", "Upload error."),

    /** An error occurred when a specific file uploading is failed. */
    UPLOAD_FILE("errUpload", "Unable to upload \"{0}\"."),

    /** An error occurred when there are no files to upload. */
    UPLOAD_NO_FILE("errUploadNoFiles", "No files found for upload."),

    /** */
    REMOVED("errRm", "Unable to remove \"{0}\"."),

    /** */
    LOCKED("errLocked", "\"{0}\" is locked and can not be renamed, moved or removed.");

    @Getter private final String key;
    private final String message;

    public String getMessage(Object... args) {
      return MessageFormat.format(this.message, args);
    }
  }
}
