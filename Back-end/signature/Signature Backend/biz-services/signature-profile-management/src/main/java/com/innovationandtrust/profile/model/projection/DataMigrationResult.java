package com.innovationandtrust.profile.model.projection;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DataMigrationResult {
  private int total = 0;
  private int fails = 0;
  private List<String> errors = new ArrayList<>();
}
