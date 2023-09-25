package com.tessi.cxm.pfl.ms5.entity.projection;

import java.util.Date;

/** A Projection for the {@link com.tessi.cxm.pfl.ms5.entity.Client} entity */
public interface ClientInfo {

  Date getCreatedAt();

  Date getLastModified();

  String getCreatedBy();

  String getLastModifiedBy();

  long getId();

  String getName();

  String getEmail();

  String getContactFirstName();

  String getContactLastname();

  String getFileId();

  String getFilename();

  long getFileSize();
}
