package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.JobDescriptionController;
import com.allweb.rms.entity.dto.JobDescriptionDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class JobDescriptionAssembler
    implements RepresentationModelAssembler<JobDescriptionDTO, EntityModel<JobDescriptionDTO>> {

  @Override
  public EntityModel<JobDescriptionDTO> toModel(JobDescriptionDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(JobDescriptionController.class).getJobDescription(entity.getId()))
            .withRel("Get Job Description by id"),
        linkTo(
                methodOn(JobDescriptionController.class)
                    .updateJonDescription(new JobDescriptionDTO()))
            .withRel("Update Job Description by id"),
        linkTo(methodOn(JobDescriptionController.class).delete(entity.getId()))
            .withRel("Delete Job Description by id"));
  }
}
