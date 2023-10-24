package com.innovationandtrust.project.model;

import com.innovationandtrust.share.utils.EntityResponseHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProjectResponse {
  private EntityResponseHandler<SignatoryProject> signatories;
  private long totalInProgress;
  private long totalDone;
}
