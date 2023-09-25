package com.tessi.cxm.pfl.ms3.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.entity.xml.Job;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {InternalConfig.class})
@Slf4j
class FlowTraceabilityXmlBinderServiceTest {

  private static final String MESSAGE = "Result should be not null.";

  @Test
  void testBindingXmlToModelObject() {
    XmlMapper xmlMapper = new XmlMapper();
    Job job = null;
    try {
      job = xmlMapper.readValue(new ClassPathResource("test.xml").getFile(), Job.class);
    } catch (IOException ignored) {
    }
    Assertions.assertNotNull(job, MESSAGE);
    log.info("Result expected => {}", job);
  }
}
