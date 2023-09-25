package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.DemandController;
import com.allweb.rms.entity.dto.DemandDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class DemandModelAssembler
    implements RepresentationModelAssembler<DemandDTO, EntityModel<DemandDTO>> {

  @Override
  public EntityModel<DemandDTO> toModel(DemandDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(DemandController.class).getDemandById(entity.getId()))
            .withRel("GetDemandById"),
        linkTo(methodOn(DemandController.class).saveDemand(DemandDTO.builder().build()))
            .withRel("CreateDemand"),
        linkTo(methodOn(DemandController.class).updateDemand(new DemandDTO()))
            .withRel("UpdateDemand"));
  }
}
