package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.dto.UserProfileProjection;
import com.tessi.cxm.pfl.ms5.entity.ProfileDetails;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileDetailsRepository extends JpaRepository<ProfileDetails, Long> {
  List<ProfileDetails> findAllByProfileId(long profileId);

  List<UserProfileProjection> findAllByProfileIdIn(Collection<Long> profileId);

  @Modifying
  @Query(
      "delete from ProfileDetails where client.id = :clientId and functionalityKey in :functionalityKeys")
  void deleteProfileDetails(
      @Param("clientId") long clientId, @Param("functionalityKeys") List<String> functionalityKeys);
}
