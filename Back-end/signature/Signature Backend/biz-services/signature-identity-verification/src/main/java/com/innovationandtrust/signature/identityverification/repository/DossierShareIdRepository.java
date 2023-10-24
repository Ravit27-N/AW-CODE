package com.innovationandtrust.signature.identityverification.repository;

import com.innovationandtrust.signature.identityverification.model.model.dossier.DossierShareId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository class for dossier share id.
 */
@Repository
public interface DossierShareIdRepository extends JpaRepository<DossierShareId, Integer> {}
