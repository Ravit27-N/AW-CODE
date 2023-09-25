package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.UniversityController;
import com.allweb.rms.entity.dto.UniversityDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class UniversityModelAssembler
    implements RepresentationModelAssembler<UniversityDTO, EntityModel<UniversityDTO>> {

  @Override
  public EntityModel<UniversityDTO> toModel(UniversityDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(UniversityController.class).getUniversityById(entity.getId()))
            .withRel("getUniversityById"),
        linkTo(methodOn(UniversityController.class).createUniversity(new UniversityDTO()))
            .withRel("create"),
        linkTo(methodOn(UniversityController.class).updateUniversity(new UniversityDTO()))
            .withRel("update"),
        linkTo(methodOn(UniversityController.class).deleteUniversityById(entity.getId()))
            .withRel("update"));
  }
}
