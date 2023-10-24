package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.RoleDto;
import com.innovationandtrust.profile.model.dto.SuperAdminDto;
import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.share.constant.RoleConstant;
import java.util.Collections;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SuperAdminUserService {

  private final UserService userService;

  private final RoleService roleService;

  private final ModelMapper modelMapper;

  public SuperAdminUserService(
      UserService userService, RoleService roleService, ModelMapper modelMapper) {
    this.userService = userService;
    this.roleService = roleService;
    this.modelMapper = modelMapper;
  }

  /**
   * Create super admin user.
   *
   * @param superAdminDTO refers to the object of superAdmin that uses to request for inserting
   */
  @Transactional(rollbackFor = Exception.class)
  public void createSuperAdminUser(SuperAdminDto superAdminDTO) {
    try {
      var user = this.modelMapper.map(superAdminDTO, UserDto.class);
      user.setRoles(Collections.singleton(RoleConstant.SUPER_ADMIN));
      this.userService.save(user);
    } catch (Exception ex) {
      log.error("Failed to init super admin user: ", ex);
    }
  }

  public boolean isUserExist(String email) {
    return this.userService.isUserExist(email);
  }

  public void initSystemRoles(Set<String> roles) {
    roles.forEach(role -> this.roleService.save(new RoleDto(0L, role)));
  }
}
