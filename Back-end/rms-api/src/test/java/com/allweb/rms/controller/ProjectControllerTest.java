package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.ProjectDTO;
import com.allweb.rms.exception.ProjectNotFoundException;
import com.allweb.rms.security.AuthenticationEntryPointJwt;
import com.allweb.rms.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AuthenticationEntryPointJwt.class)
@WebMvcTest(ProjectController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectControllerTest {

    @MockBean
    private ProjectService service;

    private ProjectDTO request;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL = "/api/v1/demand/project";

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProjectController(service)).build();
        request = new ProjectDTO(11, "CCC", "", true, false, new Date(), new Date());
    }

    @Test
    @DisplayName("when create project then return status 200")
    @Order(1)
    void testCreateProjectThenReturnStatus200() throws Exception {
        //given
        given(service.createProject(request)).willReturn(request);
        //when  //then
        this.mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this.request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("CCC")))
                .andExpect(jsonPath("$.id", is(11)));

    }

    @Test
    @DisplayName("when create candidate status then return status 400")
    @Order(2)
    void testCreateProjectThenReturnStatus400() throws Exception {
        // when //then
        this.mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new ProjectDTO())))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DisplayName("when create project then return status 409")
    @Order(3)
    void testCreateProjectThenReturnStatus409() {
        //given
        given(service.createProject(request)).willThrow(new ProjectNotFoundException());
        //when //then
        AssertionsForClassTypes.assertThatThrownBy(() -> mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())).hasCause(new ProjectNotFoundException());
    }

    @Test
    @DisplayName("when find by id Found Then return 200")
    @Order(4)
    void testFindByIdThenReturn200() throws Exception {
        //given
        given(service.getProjectById(11)).willReturn(request);
        //when //then
        this.mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 11).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("CCC")))
                .andExpect(jsonPath("$.id", is(11)));
    }

    @Test
    @DisplayName("when find by id Not Found Then Throw Exception Not Found")
    @Order(5)
    void testFindByIdThenReturn404(){
        //given
        given(service.getProjectById(11)).willThrow(new ProjectNotFoundException(1));
        //when //then throw the exception
        AssertionsForClassTypes.assertThatThrownBy(() -> this.mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 11).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())).hasCause(new ProjectNotFoundException(1));
    }

    @Test
    @DisplayName("when soft delete by id Then return status 200")
    @Order(5)
    void testSoftDeleteByIdThenReturn200() throws Exception {
        //given
        given(service.softDeleteProject(11, true)).willReturn(new ProjectDTO(11, "CCC", "", true, false, new Date(), new Date()));
        //when //then
        this.mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/delete/{isDelete}", 11, true).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("CCC"))) ///{id}/delete/{isDelete}
                .andExpect(jsonPath("$.id", is(11)));
    }
}

