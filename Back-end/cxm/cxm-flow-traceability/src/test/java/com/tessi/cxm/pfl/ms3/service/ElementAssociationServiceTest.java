package com.tessi.cxm.pfl.ms3.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDto;
import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentNotFoundException;
import com.tessi.cxm.pfl.ms3.repository.ElementAssociationRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {InternalConfig.class})
@Slf4j
class ElementAssociationServiceTest {

  private static final String TEST_USERNAME = "scour";
  private static final String MESSAGE = "Result should be not null.";
  private final ModelMapper modelMapper = new ModelMapper();
  private final FlowDocument flowDocument = ConstantProperties.FLOW_DOCUMENT;
  private final ElementAssociationDto elementAssociationDto =
      ConstantProperties.ELEMENT_ASSOCIATION_DTO;
  private final ElementAssociation elementAssociation = ConstantProperties.ELEMENT_ASSOCIATION;
  private final FlowTraceability flowTraceability = ConstantProperties.FLOW_TRACEABILITY;
  @Mock private ElementAssociationRepository elementAssociationRepository;
  @Mock private FlowDocumentRepository flowDocumentRepository;
  @Mock private FlowTraceabilityRepository flowTraceabilityRepository;
  @Mock private KeycloakService keycloakService;
  private ElementAssociationService service;

  @Mock
  private ProfileFeignClient profileFeignClient;

  @BeforeEach
  void setUp() {
    this.service =
        new ElementAssociationService(
            elementAssociationRepository, flowDocumentRepository, modelMapper, keycloakService,
            flowTraceabilityRepository);
    this.service.setProfileFeignClient(profileFeignClient);
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
  }

  @Test
  void testSaveElementAssociation() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // Set up
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      when(flowDocumentRepository.findById(anyLong())).thenReturn(Optional.of(flowDocument));
      when(elementAssociationRepository.save(any(ElementAssociation.class)))
          .thenReturn(elementAssociation);
      var result = service.save(elementAssociationDto);
      Assertions.assertNotNull(result, MESSAGE);
      log.info("Result => {}", result);
    }
  }

  @Test
  void testSaveElementAssociationFail() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // Set up
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      var result =
          Assertions.assertThrows(
              FlowDocumentNotFoundException.class, () -> service.save(elementAssociationDto));
      log.info("Result => {}", result.getMessage());
    }
  }

  @Test
  void testReturnListElementAssociation() {

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);

      when(flowDocumentRepository.findById(anyLong())).thenReturn(Optional.of(flowDocument));

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(
              new UserPrivilegeDetails("vis", "service", false, Collections.singletonList(2L)));

      when(elementAssociationRepository.findAll(
              ArgumentMatchers.<Specification<ElementAssociation>>any()))
          .thenReturn(Collections.singletonList(this.elementAssociation));

      var result = service.findAll(1);
      Assertions.assertNotNull(result, MESSAGE);
    }
  }
}
