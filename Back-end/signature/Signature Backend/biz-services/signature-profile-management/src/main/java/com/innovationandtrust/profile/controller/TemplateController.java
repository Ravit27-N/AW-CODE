package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.dto.TemplateDto;
import com.innovationandtrust.profile.model.dto.TemplateFolder;
import com.innovationandtrust.profile.model.dto.UserParticipantDto;
import com.innovationandtrust.profile.service.TemplateService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/templates")
public class TemplateController {
  private final TemplateService templateService;

  @Autowired
  public TemplateController(TemplateService templateService) {
    this.templateService = templateService;
  }

  @GetMapping
  @Tag(
      name = "Get all templates",
      description = "To get all templates grouped by folder option(search by template's name)")
  public ResponseEntity<List<TemplateFolder>> findAll(
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String search) {
    return new ResponseEntity<>(this.templateService.findAll(search), HttpStatus.OK);
  }

  @GetMapping("/corporate")
  @Tag(
      name = "Get all corporate templates",
      description = "To get all templates created by corporate admin")
  public ResponseEntity<List<TemplateDto>> findByCorporate() {
    return new ResponseEntity<>(this.templateService.findByCorporate(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  @Tag(name = "Get template by id", description = "To get template by its id")
  public ResponseEntity<TemplateDto> findById(@PathVariable long id) {
    return new ResponseEntity<>(templateService.findById(id), HttpStatus.OK);
  }

  // Get template by its id (internal use)
  @Hidden
  @GetMapping("/get/{id}")
  @Tag(name = "[Internal] Get template by its id", description = "To get template by its id")
  public ResponseEntity<Optional<TemplateDto>> getById(@PathVariable long id) {
    return new ResponseEntity<>(templateService.findTemplateById(id), HttpStatus.OK);
  }

  @PostMapping
  @Tag(name = "Create new template", description = "To create new project template (Called step 1)")
  public ResponseEntity<TemplateDto> save(@RequestBody TemplateDto templateDTO) {
    return new ResponseEntity<>(this.templateService.save(templateDTO), HttpStatus.CREATED);
  }

  @PutMapping
  @Tag(
      name = "Create new template",
      description = "To create new project template (Called step 2 and 3)")
  public ResponseEntity<TemplateDto> update(@RequestBody @Valid TemplateDto templateDto) {
    return new ResponseEntity<>(this.templateService.update(templateDto), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  @Tag(name = "Delete a template", description = "To delete template by its id")
  public ResponseEntity<Boolean> delete(@PathVariable long id) {
    this.templateService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/participants/{templateId}")
  @Tag(name = "Get participants of template")
  public ResponseEntity<List<UserParticipantDto>> getParticipants(
      @PathVariable("templateId") long templateId) {
    return new ResponseEntity<>(this.templateService.getParticipants(templateId), HttpStatus.OK);
  }

  @GetMapping("/types")
  public ResponseEntity<Set<String>> getTemplateTypes() {
    return new ResponseEntity<>(this.templateService.getTemplateTypes(), HttpStatus.OK);
  }

  @PutMapping("/favorite/{id}")
  @Tag(name = "Set template to be favorite/un-favorite")
  public ResponseEntity<Void> setFavoriteTemplate(@PathVariable("id") Long id) {
    this.templateService.setFavoriteTemplate(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Hidden
  @PutMapping("/increase-used/{id}")
  @Tag(name = "Increase template used")
  public ResponseEntity<Void> increaseTemplateUsed(@PathVariable("id") Long id) {
    this.templateService.increaseTemplateUsed(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/favorite")
  @Tag(name = "Get all user favorite and most used templates")
  public ResponseEntity<EntityResponseHandler<TemplateDto>> getUserFavoriteTemplates(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "5") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "usedCount")
          String sortField,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String search) {

    return ResponseEntity.ok(
        this.templateService.getUserFavoriteTemplates(
            PageUtils.pageable(page, pageSize, sortField, sortDirection), search));
  }
}
