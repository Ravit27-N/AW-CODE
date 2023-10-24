package com.innovationandtrust.project.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.project.model.dto.DocumentDetailRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class DocumentDetailServiceTests {
    @Mock
    DocumentDetailService documentDetailService;
    @Mock
    List<DocumentDetailRequest> documentDetailRequests;
    @BeforeEach
    void setup(){
        var documentDetailRequest = new DocumentDetailRequest();
        documentDetailRequest.setId(1L);
        documentDetailRequest.setX(1.05);
        documentDetailRequest.setY(1.05);
        documentDetailRequest.setWidth(1.05);
        documentDetailRequest.setHeight(1.99);
        documentDetailRequest.setContentType("*");
        documentDetailRequest.setFileName("abc.pdf");
        documentDetailRequest.setText("abc");
        documentDetailRequest.setTextAlign("center");
        documentDetailRequest.setFontSize(16);
        documentDetailRequest.setFontName("Poppins");
        documentDetailRequest.setPageNum(2);
        documentDetailRequest.setType("text");
        documentDetailRequest.setDocumentId(1L);
        documentDetailRequest.setSignatoryId(1L);
        documentDetailRequests.add(documentDetailRequest);
    }
    @Test
    @DisplayName("Save document detail")
    void save_document_detail_test() {
        //when
        documentDetailService.save(documentDetailRequests);
        //then
        verify(documentDetailService, times(1)).save(documentDetailRequests);
    }
}
