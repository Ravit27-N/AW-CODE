package com.allweb.rms.event;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ElasticsearchDocumentIndexNotFoundEvent extends ApplicationEvent {
  private final String indexName;

  /**
   * Create a new {@code ElasticsearchDocumentIndexNotFoundEvent}.
   *
   * @param source the object on which the event initially occurred or with which the event is
   *     associated (never {@code null})
   * @param indexName the name of the Elasticsearch document index.
   */
  public ElasticsearchDocumentIndexNotFoundEvent(Object source, String indexName) {
    super(source);
    if (StringUtils.isBlank(indexName)) {
      throw new IllegalArgumentException("Index name must not be blanked.");
    }
    this.indexName = indexName;
  }
}
