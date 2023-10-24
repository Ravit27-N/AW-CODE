package com.innovationandtrust.signature.identityverification.repository;

import com.innovationandtrust.signature.identityverification.model.model.dossier.Dossier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repository class for dossier. */
@Repository
public interface DossierRepository extends JpaRepository<Dossier, Integer> {
  Optional<Dossier> findByDossierId(String id);

  @Modifying
  @Query(
      value = "UPDATE dossiers SET dossier_shareid_id = :dossierShareIdId WHERE id = :dossierId",
      nativeQuery = true)
  void updateDossierShareIdId(
      @Param("dossierShareIdId") Integer dossierShareIdId, @Param("dossierId") Integer dossierId);
}
