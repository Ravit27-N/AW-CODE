package com.innovationandtrust.share.model.profile;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginHistoryDto implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private Long id;

  @NotEmpty private String loginEmail;

  protected Long userId;

  protected Date createdAt;
}
