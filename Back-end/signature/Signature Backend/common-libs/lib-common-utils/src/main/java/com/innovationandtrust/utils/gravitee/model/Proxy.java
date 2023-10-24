package com.innovationandtrust.utils.gravitee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Proxy {
  @JsonProperty("virtual_hosts")
  private List<VirtualHost> virtualHosts;

  private List<Group> groups;
  private Cors cors;
}
