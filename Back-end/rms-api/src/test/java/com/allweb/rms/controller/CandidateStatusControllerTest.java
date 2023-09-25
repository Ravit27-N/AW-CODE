package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.CandidateStatusDTO;
import com.allweb.rms.exception.CandidateStatusNotFoundException;
import com.allweb.rms.exception.CandidateStatusTitleConflictException;
import com.allweb.rms.security.AuthenticationEntryPointJwt;
import com.allweb.rms.service.CandidateStatusService;
import com.allweb.rms.utils.EntityResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AuthenticationEntryPointJwt.class)
@WebMvcTest(CandidateStatusController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CandidateStatusControllerTest {

    @MockBean
    private CandidateStatusService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CandidateStatusDTO request;

    // Url
    private static final String URL = "/api/v1/candidate/status";

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CandidateStatusController(service)).build();
        request = new CandidateStatusDTO(1, "In progress", false, "in progress", true, true, new Date(), new Date());
    }

    @Test
    @DisplayName("When create candidate status then return status 200")
    @Order(1)
    void testCreateCandidateStatusThenReturnStatus200() throws Exception {
        // given
        given(service.createStatusCandidate(request)).willReturn(request);
        // when // then
        this.mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("In progress")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("When create candidate status then return status 400")
    @Order(2)
    void testCreateCandidateStatusThenReturnStatus400() throws Exception {
        // when // then
        this.mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new CandidateStatusDTO())))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DisplayName("When create candidate status then return status 409")
    @Order(3)
    void testCreateCandidateStatusThenReturnStatus409() {
        // given
        given(service.createStatusCandidate(request)).willThrow(new CandidateStatusTitleConflictException());
        // when // then
        assertThatThrownBy(() -> mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isConflict())).hasCause(new CandidateStatusTitleConflictException());
    }

    @Test
    @DisplayName("When find by id Found Then return status 200")
    @Order(4)
    void testFindByIdThenReturn200() throws Exception {
        // given
        given(service.getCandidateStatusById(1)).willReturn(request);

        // when // then
        this.mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("In progress")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("When find by id Not Found Then Throw Exception Not Found")
    @Order(5)
    void testFindByIdThenReturn404() {
        // given
        given(service.getCandidateStatusById(1)).willThrow(new CandidateStatusNotFoundException(1));

        // when // then throw the exception
        assertThatThrownBy(() -> this.mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())).hasCause(new CandidateStatusNotFoundException(1));
    }

    @Test
    @DisplayName("When soft delete by id Then return status 200")
    @Order(6)
    void testSoftDeleteByIdThenReturn200() throws Exception {
        // given
        given(service.deleteCandidateStatus(1, true)).willReturn(new CandidateStatusDTO(1, "In progress", true, "in progress", true, true, new Date(), new Date()));
        // when // then
        this.mockMvc.perform(MockMvcRequestBuilders.patch(URL + "/{id}/delete/{isDelete}", 1, true).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(true)))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Test find all candidate status")
    @Order(7)
    void testFindAllCandidateStatus() throws Exception {
        // given
        List<CandidateStatusDTO> statusList = Stream.of(
                new CandidateStatusDTO(1, "In progress", false, "in progress", true, true, new Date(), new Date()),
                new CandidateStatusDTO(2, "Passed", false, "passed", true, true, new Date(), new Date())
        ).collect(Collectors.toList());
        Page<CandidateStatusDTO> statusPage = new PageImpl<>(statusList, PageRequest.of(0, 10, Sort.by(Sort.Direction.fromString("DESC"), "createdAt")), statusList.size());
        EntityResponseHandler<EntityModel<CandidateStatusDTO>> response = new EntityResponseHandler<>(statusPage.map(EntityModel::of));

        given(service.getCandidateStatus("active", 1, 10, "DESC", "createdAt", "")).willReturn(response);

        // when // then
        this.mockMvc.perform(MockMvcRequestBuilders.get(URL)
                .param("status", "active")
                .param("page", "1")
                .param("pageSize", "10")
                .param("sortDirection", "DESC")
                .param("sortByField", "createdAt")
                .param("filter", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contents[0].id", is(1)))
                .andExpect(jsonPath("$.contents[1].id", is(2)))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.pageSize", is(10)));
    }


}
