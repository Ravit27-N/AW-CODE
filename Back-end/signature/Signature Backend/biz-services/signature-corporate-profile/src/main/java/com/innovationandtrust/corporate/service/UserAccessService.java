package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.model.entity.UserAccess;
import com.innovationandtrust.corporate.repository.UserAccessRepository;
import com.innovationandtrust.share.model.corporateprofile.UserAccessDTO;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserAccessService extends CommonCrudService<UserAccessDTO, UserAccess, Long> {
  private final UserAccessRepository userAccessRepository;

  protected UserAccessService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      UserAccessRepository userAccessRepository) {
    super(modelMapper, keycloakProvider);
    this.userAccessRepository = userAccessRepository;
  }

  /**
   * To find all right accesses of end-user with pagination
   *
   * @param pageable to have pagination in response.
   * @return a list of UserAccessDTO
   */
  @Override
  @Transactional(readOnly = true)
  public Page<UserAccessDTO> findAll(Pageable pageable) {
    return this.userAccessRepository.findAll(pageable).map(this::mapData);
  }
}
