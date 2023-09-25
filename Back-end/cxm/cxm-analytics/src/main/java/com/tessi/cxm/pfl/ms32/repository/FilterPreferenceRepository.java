package com.tessi.cxm.pfl.ms32.repository;

import com.tessi.cxm.pfl.ms32.entity.UserFilterPreference;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterPreferenceRepository extends JpaRepository<UserFilterPreference, Long> {

  Optional<UserFilterPreference> findByOwnerId(@Param("ownerId") Long ownerId);
}
