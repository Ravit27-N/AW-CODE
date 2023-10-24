package com.innovationandtrust.share.model.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAfterSignRequest implements Serializable {
  private SignatoryRequest signatory;
  private List<DocumentRequest> documents = new ArrayList<>();
}
