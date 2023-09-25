package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.dto.ClientDto;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.restclient.CampaignFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.FlowFeignClient;
import com.tessi.cxm.pfl.ms5.service.restclient.TemplateFeignClient;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import java.lang.reflect.Type;
import lombok.Setter;
import org.modelmapper.ModelMapper;

/**
 * A {@code ClientService} class for functionality overriding to support mock in Unit test.
 *
 * @author Sakal TUM
 */
public class MockClientService extends ClientService {

  @Setter private boolean isSupperAdmin;
  @Setter private String userId;

  public MockClientService(
      ClientRepository clientRepository,
      UserRepository userRepository,
      ModelMapper modelMapper,
      KeycloakService keycloakService,
      CampaignFeignClient campaignFeignClient,
      TemplateFeignClient templateFeignClient,
      FlowFeignClient flowFeignClient) {
    super(
        clientRepository,
        userRepository,
        modelMapper,
        keycloakService,
        campaignFeignClient,
        templateFeignClient,
        flowFeignClient);
  }

  @Override
  public boolean isAdmin() {
    return this.isSupperAdmin;
  }

  @Override
  public boolean isSuperAdmin() {
    return this.isSupperAdmin;
  }

  @Override
  protected Type getClassType(int parameterTypeIndex) {
    if (parameterTypeIndex == 0) {
      return ClientDto.class;
    }
    return null;
  }

  @Override
  protected String getPrincipalIdentifier() {
    return this.userId;
  }
}
