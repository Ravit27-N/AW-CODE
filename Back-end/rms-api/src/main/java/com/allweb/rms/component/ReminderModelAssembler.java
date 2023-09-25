package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.ReminderController;
import com.allweb.rms.entity.dto.ReminderRequest;
import com.allweb.rms.entity.dto.ReminderResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class ReminderModelAssembler
    implements RepresentationModelAssembler<ReminderResponse, EntityModel<ReminderResponse>> {

  @Override
  public EntityModel<ReminderResponse> toModel(ReminderResponse entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(ReminderController.class).changeStatus(entity.getId(), entity.isActive()))
            .withRel(HttpMethod.PATCH.name()),
        linkTo(methodOn(ReminderController.class).getReminderById(entity.getId()))
            .withRel(HttpMethod.GET.name()),
        linkTo(
                methodOn(ReminderController.class)
                    .updateReminder(entity.getId(), new ReminderRequest()))
            .withRel(HttpMethod.PATCH.name()),
        linkTo(methodOn(ReminderController.class).softDelete(entity.getId()))
            .withRel(HttpMethod.DELETE.name()),
        linkTo(methodOn(ReminderController.class).addReminder(new ReminderRequest()))
            .withRel(HttpMethod.POST.name()));
  }
}
