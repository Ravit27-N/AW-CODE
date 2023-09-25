package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.ModuleController;
import com.allweb.rms.entity.dto.ModuleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ModuleModelAssembler
    implements RepresentationModelAssembler<ModuleDTO, EntityModel<ModuleDTO>> {
  @Override
  public EntityModel<ModuleDTO> toModel(ModuleDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(ModuleController.class).findModuleById(entity.getId()))
            .withRel("getModuleById"),
        linkTo(methodOn(ModuleController.class).createModule(new ModuleDTO())).withRel("create"),
        linkTo(methodOn(ModuleController.class).updateModule(new ModuleDTO())).withRel("update"),
        linkTo(methodOn(ModuleController.class).updateActive(entity.getId(), entity.isActive()))
            .withRel("updateActive"));
  }
}
