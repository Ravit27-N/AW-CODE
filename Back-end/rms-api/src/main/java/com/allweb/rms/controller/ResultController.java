package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.ResultDTO;
import com.allweb.rms.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/interview")
public class ResultController {
  private final ResultService resultService;

  @Autowired
  public ResultController(ResultService resultService) {
    this.resultService = resultService;
  }

  @Operation(
      operationId = "updateResult",
      description = "update result by request body",
      tags = {"Interview Result"},
      parameters = {
        @Parameter(
            in = ParameterIn.PATH,
            description = "this is the interview id",
            name = "interviewId")
      })
  @PutMapping("/{interviewId}/result")
  public ResponseEntity<ResultDTO> updateResult(
      @RequestBody @Valid ResultDTO resultDTO, @PathVariable @Valid int interviewId) {
    return new ResponseEntity<>(resultService.updateResult(resultDTO, interviewId), HttpStatus.OK);
  }

  @Operation(
      operationId = "getResultByInterviewId",
      description = "Get a result by interview id",
      tags = {"Interview Result"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "interviewId", description = "interview id")
      })
  @GetMapping("/{interviewId}/result")
  public ResponseEntity<ResultDTO> getResultByInterviewId(@PathVariable int interviewId) {
    return resultService.getResultByInterviewId(interviewId);
  }
}
