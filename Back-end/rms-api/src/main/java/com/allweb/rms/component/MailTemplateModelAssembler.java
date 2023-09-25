package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.MailTemplateController;
import com.allweb.rms.entity.dto.MailTemplateDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MailTemplateModelAssembler
    implements RepresentationModelAssembler<MailTemplateDTO, EntityModel<MailTemplateDTO>> {
  @Override
  public EntityModel<MailTemplateDTO> toModel(MailTemplateDTO entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(MailTemplateController.class).getMailTemplateById(entity.getId()))
            .withRel("getMailTemplate"),
        linkTo(methodOn(MailTemplateController.class).updateMailTemplate(new MailTemplateDTO()))
            .withRel("updateMailTemplate"),
        linkTo(methodOn(MailTemplateController.class).deleteMailTemplate(entity.getId()))
            .withRel("deleteMailTemplate"),
        linkTo(
                methodOn(MailTemplateController.class)
                    .updateActive(entity.getId(), entity.getActive()))
            .withRel("updateActive"),
        linkTo(methodOn(MailTemplateController.class).updateDeleted(entity.getId(), false))
            .withRel("updateDeleted"),
        linkTo(methodOn(MailTemplateController.class).updateIsAbleDelete(entity.getId(), false))
            .withRel("updateIsAbleDelete"));
  }
}
