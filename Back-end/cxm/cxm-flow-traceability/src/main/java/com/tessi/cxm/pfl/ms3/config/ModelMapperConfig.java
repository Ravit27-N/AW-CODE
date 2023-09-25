package com.tessi.cxm.pfl.ms3.config;

import com.tessi.cxm.pfl.ms3.dto.FlowDocumentDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentHistoryDto;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.LoadFlowDocumentDetailsDto;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.shared.model.kafka.FlowFileControlCreateFlowTraceabilityModel;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Model mapping configuration.
 *
 * @author Vichet CHANN
 * @version 1.7.0
 * @since 21 July 2022
 */
@Configuration
public class ModelMapperConfig {

  /**
   * Initialize bean of {@link ModelMapper}.
   *
   * @return the object {@link ModelMapper} after configuration
   */
  @Bean
  public ModelMapper modelMapper() {
    var modelMapper = new ModelMapper();
    modelMapper.addMappings(
        new PropertyMap<FlowFileControlCreateFlowTraceabilityModel, FlowTraceability>() {
          @Override
          protected void configure() {
            map().setId(0L);
            map().setFileId(source.getFileId());
          }
        });
    modelMapper.addMappings(
        new PropertyMap<FlowTraceability, FlowTraceabilityDto>() {
          @Override
          protected void configure() {
            map().setStep(source.getFlowTraceabilityDetails().getStep());
            map().setComposedId(source.getFlowTraceabilityDetails().getComposedId());
            map().setDepositType(source.getDepositType());
          }
        });
    modelMapper.addMappings(new PropertyMap<FlowTraceability, ListFlowTraceabilityDto>() {
      @Override
      protected void configure() {
        map().setStep(source.getFlowTraceabilityDetails().getStep());
        map().setComposedId(source.getFlowTraceabilityDetails().getComposedId());
        map().setDepositType(source.getDepositType());
        map().setCampaignName(source.getFlowTraceabilityDetails().getCampaignName());
        map().setCampaignFilename(source.getFlowTraceabilityDetails().getCampaignFilename());
      }
    });
    modelMapper.addMappings(
        new PropertyMap<FlowDocument, FlowDocumentDto>() {
          @Override
          protected void configure() {
            map().setDocName(source.getDetail().getDocName());
            map().setOwnerId(source.getFlowTraceability().getOwnerId());
          }
        });
    this.addFlowDocumentDetailMapping(modelMapper);
    return modelMapper;
  }

  /**
   * Custom mapping history of {@link FlowDocument} and {@link FlowDocumentDto}.
   *
   * @param modelMapper the object of {@link ModelMapper} to configure
   */
  private void addFlowDocumentDetailMapping(ModelMapper modelMapper) {
    modelMapper.addMappings(
        new PropertyMap<FlowDocument, LoadFlowDocumentDetailsDto>() {
          @Override
          protected void configure() {
            if (source.getFlowDocumentHistories() != null) {
              map()
                  .setHistories(
                      source.getFlowDocumentHistories().stream()
                          .map(his -> modelMapper.map(his, FlowDocumentHistoryDto.class))
                          .collect(Collectors.toSet()));

            }
          }
        });
  }
}
