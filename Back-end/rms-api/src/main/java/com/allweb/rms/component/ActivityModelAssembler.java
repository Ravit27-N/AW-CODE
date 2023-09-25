package com.allweb.rms.component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.allweb.rms.controller.ActivityController;
import com.allweb.rms.entity.dto.ActivityRequest;
import com.allweb.rms.entity.dto.ActivityResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class ActivityModelAssembler
    implements RepresentationModelAssembler<ActivityResponse, EntityModel<ActivityResponse>> {

  @Override
  public EntityModel<ActivityResponse> toModel(ActivityResponse entity) {
    return EntityModel.of(
        entity,
        linkTo(methodOn(ActivityController.class).getActivityById(entity.getId()))
            .withRel(HttpMethod.GET.name()),
        linkTo(
                methodOn(ActivityController.class)
                    .updateActivity(new ActivityRequest(), entity.getId()))
            .withRel(HttpMethod.PATCH.name()),
        linkTo(methodOn(ActivityController.class).deleteActivity(entity.getId()))
            .withRel(HttpMethod.DELETE.name()),
        linkTo(methodOn(ActivityController.class).createActivity(new ActivityRequest()))
            .withRel(HttpMethod.POST.name()));
  }
}
