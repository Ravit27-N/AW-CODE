package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.ProjectController;
import com.allweb.rms.entity.dto.ProjectDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProjectModelAssembler
    implements RepresentationModelAssembler<ProjectDTO, EntityModel<ProjectDTO>> {

  @Override
  public EntityModel<ProjectDTO> toModel(ProjectDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(ProjectController.class).getProjectById(entity.getId()))
            .withRel("getProjectById"),
        linkTo(methodOn(ProjectController.class).updateProject(new ProjectDTO())).withRel("update"),
        linkTo(
                methodOn(ProjectController.class)
                    .softDeleteProject(entity.getId(), entity.isDeleted()))
            .withRel("softDelete"),
        linkTo(
                methodOn(ProjectController.class)
                    .updateActiveProject(entity.getId(), entity.isActive()))
            .withRel("updateActive"),
        linkTo(methodOn(ProjectController.class).createProject(ProjectDTO.builder().build()))
            .withRel("create"));
  }
}
