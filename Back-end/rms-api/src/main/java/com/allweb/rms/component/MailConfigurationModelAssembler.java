package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.MailConfigurationController;
import com.allweb.rms.entity.dto.MailConfigurationDTO;
import lombok.SneakyThrows;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MailConfigurationModelAssembler
    implements RepresentationModelAssembler<
        MailConfigurationDTO, EntityModel<MailConfigurationDTO>> {
  @SneakyThrows
  @Override
  public EntityModel<MailConfigurationDTO> toModel(MailConfigurationDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(MailConfigurationController.class).getMailConfigurationById(entity.getId()))
            .withRel("Get mailConfiguration by ID"),
        linkTo(
                methodOn(MailConfigurationController.class)
                    .updateMailConfiguration(new MailConfigurationDTO()))
            .withRel("Update MailConfiguration"),
        linkTo(methodOn(MailConfigurationController.class).updateActive(entity.getId(), false))
            .withRel("Update active of mailConfiguration"),
        linkTo(methodOn(MailConfigurationController.class).updateDeleted(entity.getId(), false))
            .withRel("update Deleted of mailConfiguration"));
  }
}
