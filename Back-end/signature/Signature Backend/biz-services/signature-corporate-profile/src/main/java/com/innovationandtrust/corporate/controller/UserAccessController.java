package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.service.UserAccessService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.corporateprofile.UserAccessDTO;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user-access")
public class UserAccessController {
  private final UserAccessService userAccessService;

  @Autowired
  public UserAccessController(UserAccessService userAccessService) {
    this.userAccessService = userAccessService;
  }

  @GetMapping
  @Tag(name = "Get all user accesses", description = "To get all user accesses.")
  public ResponseEntity<EntityResponseHandler<UserAccessDTO>> findAll(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "15") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id") String sortField,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection) {
    return ResponseEntity.ok(
        new EntityResponseHandler<>(
            this.userAccessService.findAll(
                PageUtils.pageable(page, pageSize, sortField, sortDirection))));
  }
}
