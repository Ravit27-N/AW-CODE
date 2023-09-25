package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementAssociationRepository
    extends JpaRepository<ElementAssociation, Long>, JpaSpecificationExecutor<ElementAssociation> {}
