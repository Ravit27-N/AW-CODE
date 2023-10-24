package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.dto.RoleDto;
import com.innovationandtrust.profile.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/roles")
public class RoleController {
  private final RoleService roleService;

  @PostMapping
  @Tag(name = "Create new role", description = "To create new role for system")
  public ResponseEntity<RoleDto> save(@RequestBody RoleDto dto) {
    return ResponseEntity.ok(this.roleService.save(dto));
  }

  @PutMapping
  @Tag(name = "Update existing role", description = "To update existing role information")
  public ResponseEntity<RoleDto> update(@RequestBody RoleDto dto) {
    return ResponseEntity.ok(this.roleService.update(dto));
  }

  @GetMapping("/{id}")
  @Tag(name = "Get role", description = "To get role by its id")
  public ResponseEntity<RoleDto> findById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.roleService.findById(id));
  }
}
