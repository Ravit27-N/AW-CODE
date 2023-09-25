package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.UserProfileKey;
import com.tessi.cxm.pfl.ms5.entity.UserProfiles;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepository extends JpaRepository<UserProfiles, UserProfileKey> {
  @Query("select up.profile.id from UserProfiles up where up.user.technicalRef = :userId and up.user.isActive = true")
  List<Long> findAllProfileByUser(@Param("userId") String userId);

  @Query(
      "select up.user.username from UserProfiles up where up.profile = :profile and up.user.isActive = true")
  List<String> loadAllUserIdByProfileId(@Param("profile") Profile profile);

  void deleteAllByProfile(Profile profile);

  void deleteAllByUser(UserEntity userEntity);

  void deleteByProfileIn(Collection<Profile> profiles);
}
