package com.tessi.cxm.pfl.ms32.config;

import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowTraceabilityReportModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** A configuration class for defining the configuration ModelMapper. */
@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    final var modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    modelMapper.addMappings(new FlowTraceabilityReportPropertyMap());
    modelMapper.addMappings(new FlowDocumentReportPropertyMap());

    return modelMapper;
  }

  // region PropertyMap
  /**
   * A {@link PropertyMap} which define the mapping configuration from source {@link
   * CreateFlowTraceabilityReportModel} to destination {@link FlowTraceabilityReport}.
   */
  private static class FlowTraceabilityReportPropertyMap
      extends PropertyMap<CreateFlowTraceabilityReportModel, FlowTraceabilityReport> {

    @Override
    protected void configure() {
      map().setId(source.getFlowId());
    }
  }

  /**
   * A {@link PropertyMap} which define the mapping configuration from source {@link
   * CreateFlowDocumentReportModel} to destination {@link FlowDocumentReport}.
   */
  private static class FlowDocumentReportPropertyMap
      extends PropertyMap<CreateFlowDocumentReportModel, FlowDocumentReport> {

    @Override
    protected void configure() {
      map().setId(source.getDocumentId());
    }
  }

  // endregion
}
