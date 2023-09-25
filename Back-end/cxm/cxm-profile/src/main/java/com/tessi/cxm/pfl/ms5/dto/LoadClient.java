package com.tessi.cxm.pfl.ms5.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadClient implements Serializable {
  private static final long serialVersionUID = 1L;
  private long id;

  private String name;

  private String email;

  private String contactFirstName;

  private String contactLastname;

  private Date createdAt;

  private Date lastModified;

  private String createdBy;

  // Metadata of file.
  private String fileId;
  private String filename;
  private long fileSize;
}
