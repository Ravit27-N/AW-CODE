package com.tessi.cxm.pfl.ms3.controller;

import com.tessi.cxm.pfl.ms3.controller.swagger.FlowDocumentElementAssociationSwaggerConstants;
import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDto;
import com.tessi.cxm.pfl.ms3.service.ElementAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/element-association")
@AllArgsConstructor
@Tag(
    name = "Element associations of flow document",
    description = "The endpoints to manage  flow document's element associations")
public class ElementAssociationController {
  private final ElementAssociationService service;

  /**
   * Endpoint used to create {@link ElementAssociation}.
   *
   * @implNote the elementAssociation object will be validated before the process create.
   * @param dto refer to {@link ElementAssociationDto} instead of {@link ElementAssociation}.
   * @return {@link ElementAssociationDto} instead of {@link ElementAssociation} wrapped by {@link
   *     ResponseEntity}.
   * @see ElementAssociationService#save(ElementAssociationDto)
   * @see ElementAssociation
   * @see ElementAssociationDto
   * @see Validated
   * @see RequestBody
   */
  @Operation(
      operationId = "saveElementAssociation",
      summary = FlowDocumentElementAssociationSwaggerConstants.ADD_ELEMENT_ASSOCIATION_SUMMARY,
      description =
          FlowDocumentElementAssociationSwaggerConstants
              .ADD_ELEMENT_ASSOCIATION_SUMMARY_DESCRIPTION,
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description =
                  FlowDocumentElementAssociationSwaggerConstants
                      .ADD_ELEMENT_ASSOCIATION_REQUEST_BODY_DESCRIPTION),
      responses = {
        @ApiResponse(
            responseCode = "201",
            description =
                FlowDocumentElementAssociationSwaggerConstants
                    .ADD_ELEMENT_ASSOCIATION_RESPONSE_201_DESCRIPTION)
      })
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ElementAssociationDto> saveElementAssociation(
      @Validated @RequestBody ElementAssociationDto dto) {
    return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
  }

  /**
   * Endpoint used to get list of {@link ElementAssociation}.
   *
   * @param documentId refer to {@link FlowDocument} identity
   * @return {@link ElementAssociationDto} instead of {@link ElementAssociationDto} wrapped by
   *     {@link List}
   * @see ElementAssociationService#findAll(long)
   */
  @Operation(
      operationId = "findAllByDocumentId",
      summary = "Get document's element association by flow document's id.",
      description =
          FlowDocumentElementAssociationSwaggerConstants
              .GET_ELEMENT_ASSOCIATION_BY_FLOW_DOCUMENT_ID_DESCRIPTION,
      parameters =
          @Parameter(
              name = "documentId",
              in = ParameterIn.PATH,
              description = "Flow Document's id.",
              schema = @Schema(type = "integer", format = "int64", example = "1")),
      responses =
          @ApiResponse(
              responseCode = "200",
              description =
                  FlowDocumentElementAssociationSwaggerConstants
                      .GET_ELEMENT_ASSOCIATION_BY_FLOW_DOCUMENT_ID_DESCRIPTION))
  @GetMapping("/{documentId}")
  public ResponseEntity<List<ElementAssociationDto>> findAllByDocumentId(
      @PathVariable long documentId) {
    return new ResponseEntity<>(service.findAll(documentId), HttpStatus.OK);
  }
}
