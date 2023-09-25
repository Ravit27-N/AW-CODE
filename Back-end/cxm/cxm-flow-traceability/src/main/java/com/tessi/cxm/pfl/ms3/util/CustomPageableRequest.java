package com.tessi.cxm.pfl.ms3.util;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.CollectionUtils;

public class CustomPageableRequest extends PageRequest {
  /**
   * Creates a new {@link PageRequest} with sort parameters applied.
   *
   * @param page zero-based page index, must not be negative.
   * @param size the size of the page to be returned, must be greater than 0.
   * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
   */
  protected CustomPageableRequest(int page, int size, Sort sort) {
    super(page, size, sort);
  }

  public static PageRequest from(
      int page, int size, Direction direction, String property, List<String> supProperties) {
    if (!CollectionUtils.isEmpty(supProperties)) {
      return of(
          page,
          size,
          direction,
          supProperties.stream().filter(s -> s.endsWith(property)).findFirst().orElse(property));
    }
    return of(page, size, direction, property);
  }
}
