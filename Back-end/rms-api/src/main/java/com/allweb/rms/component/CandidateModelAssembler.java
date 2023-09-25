package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.ActivityController;
import com.allweb.rms.controller.CandidateController;
import com.allweb.rms.controller.InterviewController;
import com.allweb.rms.entity.dto.ActivityRequest;
import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.InterviewRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CandidateModelAssembler
    implements RepresentationModelAssembler<CandidateDTO, EntityModel<CandidateDTO>> {
  @Override
  public EntityModel<CandidateDTO> toModel(CandidateDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(CandidateController.class).getCandidateById(entity.getId()))
            .withRel("getCandidateById"),
        linkTo(
                methodOn(CandidateController.class)
                    .updateStatusCandidate(entity.getId(), entity.getStatusId()))
            .withRel("updateStatus"),
        linkTo(
                methodOn(CandidateController.class)
                    .deleteCandidate(entity.getId(), entity.isDeleted()))
            .withRel("delete"),
        linkTo(methodOn(CandidateController.class).updateCandidate(new CandidateDTO()))
            .withRel("update"),
        linkTo(methodOn(CandidateController.class).createCandidate(CandidateDTO.builder().build()))
            .withRel("create"),
        linkTo(methodOn(InterviewController.class).createInterview(new InterviewRequest()))
            .withRel("createInterview"),
        linkTo(methodOn(ActivityController.class).createActivity(new ActivityRequest()))
            .withRel("createActivity"));
  }

  public EntityModel<CandidateDTO> toViewModel(CandidateDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(CandidateController.class).getCandidateById(entity.getId()))
            .withRel("getCandidateById"),
        linkTo(
                methodOn(CandidateController.class)
                    .updateStatusCandidate(entity.getId(), entity.getStatusId()))
            .withRel("updateStatus"),
        linkTo(methodOn(CandidateController.class).updateCandidate(new CandidateDTO()))
            .withRel("update"),
        linkTo(methodOn(CandidateController.class).createCandidate(CandidateDTO.builder().build()))
            .withRel("create"),
        linkTo(methodOn(InterviewController.class).createInterview(new InterviewRequest()))
            .withRel("createInterview"),
        linkTo(methodOn(ActivityController.class).createActivity(new ActivityRequest()))
            .withRel("createActivity"));
  }
}
