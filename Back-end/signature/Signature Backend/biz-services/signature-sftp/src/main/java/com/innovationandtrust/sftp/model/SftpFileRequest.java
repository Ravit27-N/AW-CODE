package com.innovationandtrust.sftp.model;

import com.innovationandtrust.sftp.config.FileIntegrationProperty;
import com.innovationandtrust.share.model.profile.CorporateUser;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SftpFileRequest implements Serializable {

  private String flowId;
  private CorporateUser corporateUser;
  private String userUuid;
  private String filePath;
  private String filename;
  private FileIntegrationProperty integrationProperty;
  private Long timestamp;
  private String message;
  private String token;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SftpFileRequest that)) return false;
    return Objects.equals(getFlowId(), that.getFlowId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFlowId());
  }

  public String getCorporateUuid() {
    if (corporateUser != null) {
      return corporateUser.getUserEntityId();
    }
    return null;
  }
}
