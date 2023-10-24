package com.ravit.java.share.utils;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class EntityResponseHandler<T> {
  private List<T> contents;
  private int page;
  private int pageSize;
  private int totalPages;
  private long total;
  private boolean hasNext;

  public EntityResponseHandler(Page<T> page) {
    this(
        page.getContent(),
        page.getNumber() + 1,
        page.getSize(),
        page.getTotalPages(),
        page.getTotalElements(),
        page.hasNext());
  }

  public EntityResponseHandler(List<T> list) {
    this(list, 0, 0, 0, list.size(), false);
  }

  public EntityResponseHandler(
      List<T> contents, int page, int pageSize, int totalPages, long total, boolean hasNext) {
    this.contents = contents;
    this.page = page;
    this.totalPages = totalPages;
    this.pageSize = pageSize;
    this.total = total;
    this.hasNext = hasNext;
  }
}
