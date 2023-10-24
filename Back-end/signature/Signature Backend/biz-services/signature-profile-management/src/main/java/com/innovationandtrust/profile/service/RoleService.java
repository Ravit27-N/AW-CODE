package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.RoleDto;
import com.innovationandtrust.profile.model.entity.Role;
import com.innovationandtrust.profile.repository.RoleRepository;
import com.innovationandtrust.profile.service.spefication.RoleSpec;
import com.innovationandtrust.share.constant.SignatureRole;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService extends CommonCrudService<RoleDto, Role, Long> {
  private final RoleRepository roleRepository;

  protected RoleService(
      final ModelMapper modelMapper,
      RoleRepository roleRepository,
      IKeycloakProvider keycloakProvider) {
    super(modelMapper, keycloakProvider);
    this.roleRepository = roleRepository;
  }

  /**
   * To create a role
   *
   * @param dto refers to roleDTO use to request to create a new role
   * @return RoleDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public RoleDto save(RoleDto dto) {
    var keycloakRole =
        this.keycloakProvider.getRoles().stream()
            .filter(role -> Objects.equals(role.getName(), dto.getName()))
            .findFirst()
            .orElse(this.keycloakProvider.addRealmRole(dto.getName()));
    var role = this.findByKeycloakRoleId(keycloakRole.getId()).orElse(new Role());
    if (Objects.isNull(role.getId()) || role.getId() == 0) {
      var roleEntity = this.mapEntity(dto, role);
      roleEntity.setKeycloakRoleId(keycloakRole.getId());
      if (!SignatureRole.isSystemRole(role.getName())) {
        roleEntity.setCreatedBy(this.getUserId());
      } else {
        roleEntity.setCreatedBy(0L);
      }
      roleEntity.setModifiedBy(0L);
      return this.mapData(this.roleRepository.save(roleEntity));
    }
    return this.mapData(role);
  }

  private Optional<Role> findByKeycloakRoleId(String id) {
    return this.roleRepository.findOne(Specification.where(RoleSpec.findByKeycloakRole(id)));
  }

  /**
   * Find by role name
   *
   * @param names refers to a set of role names
   * @return a list of Role
   */
  public List<RoleDto> findByNames(Set<String> names) {
    return this.roleRepository.findAll(Specification.where(RoleSpec.findByNames(names))).stream()
        .map(this::mapData)
        .toList();
  }
}
