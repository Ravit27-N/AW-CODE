package com.innovationandtrust.project.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.project.model.dto.DocumentContent;
import com.innovationandtrust.project.service.DocumentService;
import com.innovationandtrust.utils.file.provider.FileProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileControllerTests {
  @Mock MockMvc mockMvc;
  @InjectMocks FileController fileController;
  @Mock
  DocumentService documentService;
  @Mock DocumentContent documentContent;

  @Mock
  FileProvider fileProvider;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());

    mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();

    documentContent = new DocumentContent();
    documentContent.setContentType("application/pdf");
    documentContent.setContentLength(1200L);
    documentContent.setFileName("test.pdf");
  }

  @DisplayName("Upload files api test")
  @Test
  void upload_files_api_test() throws Exception {
    MultipartFile[] files = new MultipartFile[] {
            new MockMultipartFile("file1", "file1.txt", "text/plain", "Hello, World!".getBytes()),
            new MockMultipartFile("file2", "file2.txt", "text/plain", "Goodbye, World!".getBytes())
    };

    byte[] bytes=files[0].getBytes();
    fileProvider.uploads(files, "test");
    mockMvc.perform(multipart("/v1/documents/uploads")
            .file("files", bytes)
            .param("dirs", "test"))
            .andExpect(status().isOk());
  }

  @DisplayName("View document by id api test")
  @Test
  void download_by_id_api_test() throws Exception {
    // when
    when(documentService.downloadDocument(1L)).thenReturn(documentContent);

    mockMvc.perform(get("/v1/documents/{id}/download/current", 1L)).andExpect(status().isOk());

    // then
    verify(documentService, times(1)).downloadDocument(1L);
  }
}
