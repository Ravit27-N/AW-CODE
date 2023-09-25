package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.CandidateStatusController;
import com.allweb.rms.entity.dto.CandidateStatusDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CandidateStatusModelAssembler
    implements RepresentationModelAssembler<CandidateStatusDTO, EntityModel<CandidateStatusDTO>> {

  @Override
  public EntityModel<CandidateStatusDTO> toModel(CandidateStatusDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(CandidateStatusController.class).getCandidateStatusById(entity.getId()))
            .withRel("getStatusById"),
        linkTo(
                methodOn(CandidateStatusController.class)
                    .updateCandidateStatus(new CandidateStatusDTO()))
            .withRel("update"),
        linkTo(
                methodOn(CandidateStatusController.class)
                    .deleteStatusCandidate(entity.getId(), entity.isDeleted()))
            .withRel("delete"),
        linkTo(
                methodOn(CandidateStatusController.class)
                    .updateActiveStatusCandidate(entity.getId(), entity.isActive()))
            .withRel("updateActive"),
        linkTo(
                methodOn(CandidateStatusController.class)
                    .createStatusCandidate(CandidateStatusDTO.builder().build()))
            .withRel("create"));
  }
}
