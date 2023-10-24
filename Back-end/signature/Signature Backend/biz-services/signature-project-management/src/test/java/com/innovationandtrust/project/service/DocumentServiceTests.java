package com.innovationandtrust.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.project.model.dto.DocumentContent;
import com.innovationandtrust.project.model.dto.DocumentDTO;
import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.model.entity.Document;
import com.innovationandtrust.share.model.project.DocumentRequest;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.file.model.FileResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Slf4j
class DocumentServiceTests {
  @Mock DocumentService documentService;
  @Mock Document document;
  @Mock List<FileResponse> fileResponses;
  @Mock List<DocumentDTO> documentDTOList;
  @Mock DocumentDTO dto;
  @Mock
  DocumentContent content;
  @Mock DocumentContent documentContent;
  @Mock List<SignatoryDto> signatoryDtos;
  @Mock
  Resource resource;

  @BeforeEach
  public void setUp() {
    document = new Document();
    document.setId(1L);
    document.setFileName("bb0841d1-39b4-4a31-9d09-6b21de03a7b2.pdf");
    document.setOriginalFileName("file.pdf");
    document.setSignedDocUrl("http://localhost:3000/sign-doc");
    document.setEditedFileName("edited-bb0841d1-39b4-4a31-9d09-6b21de03a7b2.pdf");
    document.setContentType("application/pdf");
    document.setFullPath("E:\\upload\\file\\bb0841d1-39b4-4a31-9d09-6b21de03a7b2.pdf");
    document.setExtension("pdf");
    document.setTotalPages(10);

    dto = new DocumentDTO();
    dto.setId(1L);
    dto.setFileName("bb0841d1-39b4-4a31-9d09-6b21de03a7b2.pdf");
    dto.setOriginalFileName("file.pdf");
    dto.setSignedDocUrl("http://localhost:3000/sign-doc");
    dto.setEditedFileName("edited-bb0841d1-39b4-4a31-9d09-6b21de03a7b2.pdf");
    dto.setContentType("application/pdf");
    dto.setFullPath("E:\\upload\\file\\bb0841d1-39b4-4a31-9d09-6b21de03a7b2.pdf");
    dto.setExtension("pdf");
    dto.setTotalPages(10);
  }

  @DisplayName("Save document")
  @Test
  void save_document() {
    // given
    var projectId = 168L;

    // when
    when(documentDTOList.size()).thenReturn(5);
    when(documentService.save(fileResponses, projectId)).thenReturn(documentDTOList);

    List<DocumentDTO> result = documentService.save(fileResponses, 168L);

    // then
    assertThat(result).isNotNull();
    assertEquals(5, result.size());
    verify(documentService, times(1)).save(fileResponses, 168L);
  }

  @DisplayName("Update document")
  @Test
  void update_document() {
    // when
    when(documentService.update(dto)).thenReturn(dto);

    DocumentDTO result = documentService.update(dto);

    // then
    assertThat(result).isNotNull();
    assertEquals(dto.getFileName(), result.getFileName());
    verify(documentService, times(1)).update(dto);
  }

  @DisplayName("View document in base64")
  @Test
  void view_document_in_base64() {
    // given
    String docName = "abc.pdf";

    // when
    when(documentService.viewDocumentBase64(docName)).thenReturn(anyString());

    String encodeFile = documentService.viewDocumentBase64(docName);

    // then
    assertThat(encodeFile).isNotNull();
    verify(documentService, times(1)).viewDocumentBase64(docName);
  }

  @DisplayName("view document")
  @Test
  void view_document() {
    // given
    String docName = "abc.pdf";

    // when
    when(documentService.viewDocument(docName)).thenReturn(documentContent);

    DocumentContent result = documentService.viewDocument(docName);

    // then
    assertThat(result).isNotNull();
    verify(documentService, times(1)).viewDocument(docName);
  }

  @DisplayName("Download document")
  @Test
  void download_document() {
    //given
    Long docId = 1L;

    //when
    when(documentService.downloadDocument(docId)).thenReturn(content);

    DocumentContent result = documentService.downloadDocument(docId);

    //then
    assertThat(result).isNotNull();
    verify(documentService, times(1)).downloadDocument(1L);
  }

  @DisplayName("Encode file to base64")
  @Test
  void encode_file_to_base64() {
    //when
    when(documentService.encodeFileToBase64(resource)).thenReturn(anyString());

    String encodeFile = documentService.encodeFileToBase64(resource);

    //then
    assertThat(encodeFile).isNotNull();
    verify(documentService, times(1)).encodeFileToBase64(resource);
  }

  @DisplayName("Find by Id")
  @Test
  void find_by_id() {
    // given
    Long id = 1L;

    // when
    when(documentService.findById(id)).thenReturn(dto);

    DocumentDTO result = documentService.findById(id);

    // then
    assertThat(result).isNotNull();
    assertEquals(dto.getFileName(), result.getFileName());
    verify(documentService, times(1)).findById(1L);
  }

  @DisplayName("Update signed document url")
  @Test
  void update_signed_document_url() {
    var documentRequests =
        new ProjectAfterSignRequest(
            new SignatoryRequest(),
            List.of(new DocumentRequest(1L, "/api/v1/session/1/documents/3")));
    documentService.updateSignedDocUrl(documentRequests);

    // then
    verify(documentService, times(1)).updateSignedDocUrl(documentRequests);
  }

  @Nested
  @DisplayName("Get all documents by project Id")
  class GetAllDocumentsByProjectId {
    @DisplayName("When project id found in database")
    @Test
    void get_all_documents_by_project_id() {
      // given
      var projectId = 168L;

      // when
      when(documentService.findAllByProjectId(projectId)).thenReturn(documentDTOList);
      when(documentDTOList.size()).thenReturn(5);

      List<DocumentDTO> result = documentService.findAllByProjectId(168L);

      // then
      assertEquals(5, result.size());
      verify(documentService, times(1)).findAllByProjectId(168L);
    }
  }

  @Nested
  @DisplayName("Find document entity by id")
  class FindDocumentEntityById {
    @DisplayName("When document with the given id is found")
    @Test
    void find_entity_by_id() {
      // when
      when(documentService.findEntityById(1L)).thenReturn(document);

      Document result = documentService.findEntityById(1L);

      // then
      assertThat(result).isNotNull();
      assertEquals(document.getFileName(), result.getFileName());
      verify(documentService, times(1)).findEntityById(1L);
    }
  }
}
