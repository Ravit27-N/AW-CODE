package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.ResultController;
import com.allweb.rms.entity.dto.ResultDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ResultModelAssembler
    implements RepresentationModelAssembler<ResultDTO, EntityModel<ResultDTO>> {

  @Override
  public EntityModel<ResultDTO> toModel(ResultDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(
                methodOn(ResultController.class)
                    .updateResult(new ResultDTO(), entity.getInterviewId()))
            .withRel("update"),
        linkTo(methodOn(ResultController.class).getResultByInterviewId(entity.getInterviewId()))
            .withRel("getResultByInterviewId"));
  }
}
