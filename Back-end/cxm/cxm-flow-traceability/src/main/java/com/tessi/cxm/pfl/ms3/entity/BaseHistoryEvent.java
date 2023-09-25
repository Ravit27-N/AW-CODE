package com.tessi.cxm.pfl.ms3.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Sokhour LACH
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass

public class BaseHistoryEvent extends BaseEntity {
  protected String event;
  protected String server;
  protected Date dateTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseHistoryEvent)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    BaseHistoryEvent that = (BaseHistoryEvent) o;
    return getEvent().equals(that.getEvent());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getEvent());
  }
}
