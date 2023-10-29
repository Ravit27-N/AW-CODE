package com.ravit.java.controller;


import com.ravit.java.model.Candidate;
import com.ravit.java.model.dto.CandidateDto;
import com.ravit.java.service.CandidateService;
import com.ravit.java.share.constant.CommonParamsConstant;
import com.ravit.java.share.utils.EntityResponseHandler;
import com.ravit.java.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/candidate")
@RequiredArgsConstructor
@Tag(name = "Candidate Controler", description = "The API endpoints tom manage candidate")
public class CandidateController {

  private final CandidateService candidateService;

  @GetMapping
  public ResponseEntity<EntityResponseHandler<CandidateDto>> findAll(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
      String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
      String sortByField) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.candidateService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection))),
        HttpStatus.OK);
  }


  @PostMapping
  public ResponseEntity<CandidateDto> save(@RequestBody @Valid CandidateDto dto) {
    return new ResponseEntity<>(this.candidateService.save(dto), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CandidateDto> findById(@PathVariable Long id) {
    return new ResponseEntity<>(this.candidateService.findById(id), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<CandidateDto> update(@RequestBody @Valid CandidateDto dto) {
    return new ResponseEntity<>(this.candidateService.update(dto), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    this.candidateService.delete(id);
  }

}
