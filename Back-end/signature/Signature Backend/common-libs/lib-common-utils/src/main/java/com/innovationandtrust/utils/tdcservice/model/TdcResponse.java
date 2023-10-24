package com.innovationandtrust.utils.tdcservice.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TdcResponse {
    private String welcomeClass;
    private UUID uuid;
    private UUID baseUUID;
    private String baseId;
    private long creationDate;
    private long archivageDate;
    private long modificationDate;
    private boolean hasNote;
    private boolean inRecycleBin;
    private boolean frozen;
    private TdcCriterion[] criterions;
    private Object[] attachments;
    private String indexableType;
    private Object customNextStepExecutionDate;
    private Object customNextStepUUID;
    private long nextStepExecutionDate;
    private Object title;
    private UUID fileID;
    private String repositoryName;
    private String digest;
    private String digestAlgorithm;
    private String version;
    private String filename;
    private String extension;
    private boolean virtual;
    private long size;
    private long startPage;
    private long endPage;
    private String type;
    private Object disposalDate;
    private Object keyReferenceUUID;
    private Object encryptedKeyUUID;
    private Object overlayName;
    private Object vaultID;
    private Object externalID;
    private Object archivalStatus;
    private Object archivalFate;
    private Object archivalServiceProducer;
    private Object archivalServiceSubmission;
    private Object archivalFateDate;
    private Object archivalBatchID;
}
