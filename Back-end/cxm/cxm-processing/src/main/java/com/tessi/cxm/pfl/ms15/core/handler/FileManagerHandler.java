package com.tessi.cxm.pfl.ms15.core.handler;

import com.tessi.cxm.pfl.ms15.constant.ProcessingConstant;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileManagerHandler extends AbstractExecutionHandler {
  public static final String OPTION_KEY = "OPTION_KEY";
  private final FileManagerResource fileManagerResource;
  private final FileService fileService;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    Option option = context.get(OPTION_KEY, Option.class);
    var ownerId = context.get(ProcessingConstant.ID_CREATOR, Long.class);
    var token = context.get(ProcessingConstant.TOKEN_KEY, String.class);
    var fileId = context.get(ProcessingConstant.FILE_ID, String.class);
    var funcKey = context.get(ProcessingConstant.FUNC_KEY, String.class);
    var privKey = context.get(ProcessingConstant.PRIV_KEY, String.class);
    if (Objects.equals(Option.GET, option)) {
      var base64 = getFile(fileId, funcKey, privKey, token);
      context.put(ProcessingConstant.BASE64_FILE, base64);
    } else if (Objects.equals(Option.POST, option)) {
      var filename = context.get(ProcessingConstant.TMP_FILE_NAME, String.class);
      var pathFile = context.get(ProcessingConstant.TMP_FILE, Path.class).resolve(filename);
      var uploadFileId = context.get(ProcessingConstant.UPLOAD_FILE_ID, String.class);
      uploadFile(fileId, uploadFileId, pathFile.toFile(), ownerId, funcKey, privKey, token);
    } else {
      log.info("No option to operate.");
    }
    return ExecutionState.NEXT;
  }

  private String getFile(String fileId, String funcKey, String privKey, String token) {
    return this.fileManagerResource.getFile(fileId, funcKey, privKey, token).getContent();
  }

  private void uploadFile(
      String refFile,
      String fileId,
      File sources,
      Long idCreator,
      String funcKey,
      String privKey,
      String token) {
    try {
      final MultipartFile multipartFile = this.fileService.fileToMultipartFile(sources);
      this.fileManagerResource.uploadFile(
          multipartFile, idCreator, fileId, refFile, "", 0, false, funcKey, privKey, token);
      log.info("The file has been uploaded with the id {}", fileId);
    } catch (IOException e) {
      log.error("Unable to convert file multipartFile because %s", e);
      throw new FileErrorException(
          String.format("Unable to convert file multipartFile because %s", e.getMessage()));
    }
  }

  @RequiredArgsConstructor
  @Getter
  public enum Option {
    GET("Get", "For get file."),
    POST("Post", "For upload file.");
    private final String value;
    private final String description;
  }
}
