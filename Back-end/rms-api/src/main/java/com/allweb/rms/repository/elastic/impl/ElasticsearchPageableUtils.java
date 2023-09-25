package com.allweb.rms.repository.elastic.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class ElasticsearchPageableUtils {

  private ElasticsearchPageableUtils() {}

  /**
   * Prefix field with with "keyword" for unsupported sortable data types like text or date.
   *
   * @param sourcePageable Source {@link Pageable} witch include {@link Sort} details.
   * @param supportedSortFields Fields allows to be prefixed. Throws {@link
   *     IllegalArgumentException} if one of a field in {@code sourcePageable} in not contains in
   *     {@code supportedSortFields}.
   * @return Keyword prefixed {@link Pageable}.
   */
  public static Pageable resolveKeywordPrefixedPageable(
      Pageable sourcePageable, String[] supportedSortFields) {
    List<Sort.Order> orderList = new ArrayList<>();
    sourcePageable
        .getSort()
        .forEach(
            order -> {
              if (!ArrayUtils.contains(supportedSortFields, order.getProperty())) {
                throw new IllegalArgumentException(
                    String.format("Sort by \"%s\" is not supported.", order.getProperty()));
              }
              String keywordPrefixedField = String.format("%s.keyword", order.getProperty());
              orderList.add(
                  order.isAscending()
                      ? Sort.Order.asc(keywordPrefixedField)
                      : Sort.Order.desc(keywordPrefixedField));
            });
    return PageRequest.of(
        sourcePageable.getPageNumber(), sourcePageable.getPageSize(), Sort.by(orderList));
  }
}
