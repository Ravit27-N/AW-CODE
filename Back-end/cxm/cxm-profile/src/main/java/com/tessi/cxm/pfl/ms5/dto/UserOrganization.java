package com.tessi.cxm.pfl.ms5.dto;

import com.tessi.cxm.pfl.shared.utils.TupleUtils;
import java.io.Serializable;
import javax.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class UserOrganization implements Serializable {
  private Long serviceId;
  private String serviceName;
  private Long divisionId;
  private String divisionName;
  private Long clientId;
  private String clientName;

  public UserOrganization(Tuple tuple) {
    this.serviceId = TupleUtils.getValue(tuple, 0, Long.class);
    this.serviceName = TupleUtils.getValue(tuple, 1, String.class);
    this.divisionId = TupleUtils.getValue(tuple, 2, Long.class);
    this.divisionName = TupleUtils.getValue(tuple, 3, String.class);
    this.clientId = TupleUtils.getValue(tuple, 4, Long.class);
    this.clientName = TupleUtils.getValue(tuple, 5, String.class);
  }
}
