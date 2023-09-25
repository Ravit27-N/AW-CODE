package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.InterviewStatusController;
import com.allweb.rms.entity.dto.InterviewStatusDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class InterviewStatusAssembler
    implements RepresentationModelAssembler<InterviewStatusDTO, EntityModel<InterviewStatusDTO>> {
  @Override
  public EntityModel<InterviewStatusDTO> toModel(InterviewStatusDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(InterviewStatusController.class).getStatusById(entity.getId()))
            .withRel("Get Status by id"),
        linkTo(methodOn(InterviewStatusController.class).updateStatus(new InterviewStatusDTO()))
            .withRel("Update Status by id"),
        linkTo(methodOn(InterviewStatusController.class).deleteStatus(entity.getId()))
            .withRel("Delete Status by id"));
  }
}
