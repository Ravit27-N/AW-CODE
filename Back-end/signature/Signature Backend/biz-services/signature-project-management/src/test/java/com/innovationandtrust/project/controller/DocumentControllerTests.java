package com.innovationandtrust.project.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.project.ProjectManagementApplication;
import com.innovationandtrust.project.model.dto.DocumentDTO;
import com.innovationandtrust.project.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = ProjectManagementApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class DocumentControllerTests {
  @Mock MockMvc mockMvc;
  @Mock
  DocumentService documentService;
  @Mock DocumentDTO dto;
  @InjectMocks DocumentController documentController;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());

    mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();

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

  @DisplayName("Find document by id")
  @Test
  void find_document_by_id() throws Exception {
    // given
    Long id = 1L;

    // when
    when(documentService.findById(id)).thenReturn(dto);

    // then
    mockMvc
        .perform(get("/documents/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)));

    verify(documentService, times(1)).findById(1L);
  }
}
