package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.ProfileDetails;
import com.tessi.cxm.pfl.shared.service.SharedRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository
    extends JpaRepository<Profile, Long>,
    JpaSpecificationExecutor<Profile>,
    SharedRepository<Long> {

  boolean existsAllByNameIgnoreCaseAndClientId(String name, long clientId);

  boolean existsAllByNameIgnoreCase(String name);

  @Query(
      "select p.profileDetails from Profile p inner join p.profileDetails pd on p.id = pd.profile.id "
          + " where pd.profile.id in :profileIds and pd.functionalityKey = :funcKey")
  List<ProfileDetails> getAllPrivilegesOfProfile(
      @Param("profileIds") Collection<Long> profileIds, @Param("funcKey") String functionalKey);

  List<Profile> findAllByCreatedByIn(Collection<String> createdBy);

  void deleteByClientId(Long clientId);

  List<Profile> findByClientId(Long clientId);


  Optional<Profile> findByClientIdAndCreatedBy(long clientId, String createdBy);

  @EntityGraph(type = EntityGraphType.FETCH, value = "ProfileInfo")
  Optional<Profile> findById(Long id);

  @EntityGraph(type = EntityGraphType.FETCH, value = "ProfileList")
  Page<Profile> findAll(@Nullable Specification<Profile> spec, Pageable pageable);

  @EntityGraph(type = EntityGraphType.FETCH, value = "ProfileList")
  List<Profile> findAll(@Nullable Specification<Profile> spec);
  @Query("select p from Profile p where lower(p.name) in (:profileNames) and p.client.id = :clientId")
  List<Profile> findProfiles(@Param("profileNames") List<String> profileNames, @Param("clientId") Long clientId);
}
