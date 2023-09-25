package com.allweb.rms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Data
@Builder
@ToString
@EqualsAndHashCode
public class EntityResponseHandler<T> {
  private final List<T> contents;
  private int page;
  private int pageSize;
  private long total;

  public EntityResponseHandler(Page<T> page) {
    this(page.getContent(), page.getNumber() + 1, page.getSize(), page.getTotalElements());
  }

  public EntityResponseHandler(Page<T> page, ObjectMapper mapper) throws JsonProcessingException {
    this(
        mapper.readValue(
            mapper.writeValueAsString(page.getContent()), new TypeReference<List<T>>() {}),
        page.getNumber() + 1,
        page.getSize(),
        page.getTotalElements());
  }

  public EntityResponseHandler(List<T> list) {
    this(list, 0, 0, list.size());
  }

  public EntityResponseHandler(List<T> contents, int page, int pageSize, long total) {
    this.contents = contents;
    this.page = page;
    this.pageSize = pageSize;
    this.total = total;
  }
}
