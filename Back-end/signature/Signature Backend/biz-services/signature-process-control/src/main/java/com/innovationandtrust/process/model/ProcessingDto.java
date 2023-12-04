package com.innovationandtrust.process.model;

import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// This class use processing on single participant reference to project
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingDto {
  private Project project;
  private Participant participant;
  // Current project owner responsibility
  private User user;
}
