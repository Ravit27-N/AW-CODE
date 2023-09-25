package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.UserHub;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHubRepository extends JpaRepository<UserHub, Long> {

  @Query("select u from UserHub u "
      + "inner join Client c on c.id = u.client.id "
      + "inner join Division d on d.client.id = c.id "
      + "inner join Department de on de.division.id = d.id "
      + "inner join UserEntity ue on ue.department.id = de.id "
      + "where ue.technicalRef = :technicalRef and ue.isActive = true ")
  Optional<UserHub> findByTechnicalRef(@Param("technicalRef") String technicalRef);

  @Query("select u from UserHub u "
      + "inner join Client c on c.id = u.client.id "
      + "inner join Division d on d.client.id = c.id "
      + "inner join Department de on de.division.id = d.id "
      + "inner join UserEntity ue on ue.department.id = de.id "
      + "where ue.username = :username and ue.isActive = true")
  Optional<UserHub> findByUserEntityUsername(@Param("username") String username);

  Optional<UserHub> findByClientId(Long clientId);

  Page<UserHub> findAllByEncryptedFalse(Pageable pageable);
}
