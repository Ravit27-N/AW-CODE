package com.tessi.cxm.pfl.ms3.core.batch.writer;

import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;

@Slf4j
public class FlowTraceabilityItemWriter implements ItemWriter<FlowTraceability> {

  private final JpaItemWriter<FlowTraceability> jpaItemWriter;

  public FlowTraceabilityItemWriter(JpaItemWriter<FlowTraceability> jpaItemWriter) {
    this.jpaItemWriter = jpaItemWriter;
  }


  @Override
  public void write(List<? extends FlowTraceability> items) throws Exception {
    this.jpaItemWriter.write(items);
  }
}
