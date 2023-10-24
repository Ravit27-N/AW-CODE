package com.innovationandtrust.project.repository;

import com.innovationandtrust.project.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** FileRepository. */
public interface FileRepository
    extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {}
