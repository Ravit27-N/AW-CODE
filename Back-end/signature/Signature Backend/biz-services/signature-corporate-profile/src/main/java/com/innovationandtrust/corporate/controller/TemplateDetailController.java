package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.model.dto.TemplateDetailDto;
import com.innovationandtrust.corporate.service.TemplateDetailService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/template-details")
@RequiredArgsConstructor
public class TemplateDetailController {
  private final TemplateDetailService templateDetailService;

  @GetMapping
  @Tag(name = "Get list of template details", description = "To get list of template details.")
  public ResponseEntity<EntityResponseHandler<TemplateDetailDto>> list(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "15") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField) {

    return ResponseEntity.ok(
        new EntityResponseHandler<>(
            this.templateDetailService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection))));
  }

  @PostMapping
  @Tag(name = "Create template detail", description = "To create template detail.")
  public ResponseEntity<TemplateDetailDto> save(@RequestBody @Valid TemplateDetailDto dto) {
    return new ResponseEntity<>(this.templateDetailService.save(dto), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Tag(name = "Get template detail by ID", description = "To get template detail by ID.")
  public ResponseEntity<TemplateDetailDto> findById(@PathVariable Long id) {
    return new ResponseEntity<>(this.templateDetailService.findById(id), HttpStatus.OK);
  }

  @PutMapping
  @Tag(name = "Update template detail", description = "To update template detail.")
  public ResponseEntity<TemplateDetailDto> update(@RequestBody @Valid TemplateDetailDto dto) {
    return new ResponseEntity<>(this.templateDetailService.update(dto), HttpStatus.CREATED);
  }
}
