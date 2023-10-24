package com.innovationandtrust.project.repository;

import com.innovationandtrust.project.model.entity.SignedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** SignedDocumentRepository. */
public interface SignedDocumentRepository
    extends JpaRepository<SignedDocument, Long>, JpaSpecificationExecutor<SignedDocument> {}
