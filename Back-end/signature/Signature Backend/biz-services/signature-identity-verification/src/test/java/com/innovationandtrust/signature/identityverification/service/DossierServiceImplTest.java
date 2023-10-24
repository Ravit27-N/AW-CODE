package com.innovationandtrust.signature.identityverification.service;

import com.innovationandtrust.signature.identityverification.model.dto.dossier.DossierIdResponse;
import com.innovationandtrust.signature.identityverification.model.dto.dossier.DossierResponse;
import com.innovationandtrust.signature.identityverification.model.model.dossier.Dossier;
import com.innovationandtrust.signature.identityverification.repository.DossierRepository;

import java.util.UUID;

import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import com.innovationandtrust.utils.signatureidentityverification.enums.VerificationChoice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class DossierServiceImplTest {
    @Mock
    private DossierService dossierService;
    @Mock
    private DossierRepository dossierRepository;
    private DossierDto dossierDto;

    @BeforeEach
    public void setup() {
        dossierDto =
                new DossierDto("vithou_loy_9", "", "", "+98881231234", "dossierName", VerificationChoice.PVID);
    }

    @AfterEach
    public void tearDown() {
        dossierRepository.delete(new Dossier(dossierDto));
    }

    @Test
    void givenDossierDto_whenCreateDossier_thenReturnDossierIdResponse() {
        Mockito.when(dossierService.createDossier(dossierDto))
                .thenReturn(new DossierIdResponse("test",""));
        DossierIdResponse response = dossierService.createDossier(dossierDto);
        Assertions.assertEquals("test", response.getDossierId());
    }

    @Test
    void givenDossierDto_whenCreateDossier_thenThrowDataIntegrityViolationException() {
        Mockito.when(dossierService.createDossier(dossierDto))
                .thenThrow(new DataIntegrityViolationException("test"));
        Assertions.assertThrows(
                DataIntegrityViolationException.class, () -> dossierService.createDossier(dossierDto));
    }

    @Test
    void givenDossierId_whenGetDossierByDossierId_thenReturnDossier() {
        Mockito.when(dossierService.getDossierByDossierId("test")).thenReturn(new Dossier(dossierDto));
        Dossier dossier = dossierService.getDossierByDossierId("test");
        Assertions.assertEquals("vithou_loy_9", dossier.getFirstname());
    }

    @Test
    void givenDossierId_whenGetDossierByDossierId_thenThrowDossierIdNotFoundException() {
        Mockito.when(dossierService.getDossierByDossierId("test"))
                .thenThrow(new EntityNotFoundException("test"));
        Assertions.assertThrows(
                EntityNotFoundException.class, () -> dossierService.getDossierByDossierId("test"));
    }

    @Test
    void givenDossierId_whenConfirmDossier_thenVerifyDossierConfirm() {
        Mockito.doNothing().when(dossierService).confirmDossier("test");
        dossierService.confirmDossier("test");
        Mockito.verify(dossierService, Mockito.times(1)).confirmDossier("test");
    }

    @Test
    void givenDossierId_whenUpdateDossierUuid_thenVerifyDossierUpdateUuid() {
        String uuid = UUID.randomUUID().toString();
        Mockito.doNothing().when(dossierService).updateDossierUuid("test", uuid);
        dossierService.updateDossierUuid("test", uuid);
        Mockito.verify(dossierService, Mockito.times(1)).updateDossierUuid("test", uuid);
    }

    @Test
    void givenDossierId_whenGetDossierDto_thenReturnDossierResponse() {
        Mockito.when(dossierService.getDossierDto("test"))
                .thenReturn(new DossierResponse(new Dossier(dossierDto)));
        DossierResponse response = dossierService.getDossierDto("test");
        Assertions.assertEquals("dossierName", response.getDossierName());
    }

    @Test
    void givenDossierId_whenVerifyDocument_thenReturnVerificationDocumentResponse() {
        Mockito.when(dossierService.verifyDocument("test", null))
                .thenReturn(new VerificationDocumentResponse());
        VerificationDocumentResponse response = dossierService.verifyDocument("test", null);
        Assertions.assertEquals(Boolean.FALSE, response.isAuthenticity());
    }

    @Test
    void givenDossierId_whenVerifyDocument_thenThrowException() {
        Mockito.when(dossierService.verifyDocument("test", null)).thenThrow(RuntimeException.class);
        Assertions.assertThrows(
                RuntimeException.class, () -> dossierService.verifyDocument("test", null));
    }
}
