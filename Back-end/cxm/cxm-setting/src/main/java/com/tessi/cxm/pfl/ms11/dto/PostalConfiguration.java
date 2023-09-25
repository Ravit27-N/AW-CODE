package com.tessi.cxm.pfl.ms11.dto;

import com.tessi.cxm.pfl.shared.model.Configuration;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class PostalConfiguration {

  private final List<Configuration> configurations = new ArrayList<>();
}
