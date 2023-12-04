package com.innovationandtrust.process.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/** Class about support to upload signature file for signing multiple projects */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadSignatureFileDto {
  // refers to signature file
  private MultipartFile file;
  private List<SigningProcessDto> projects;
}
