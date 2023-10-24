package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.UserActivity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository
    extends JpaRepository<UserActivity, Long>, JpaSpecificationExecutor<UserActivity> {
  Optional<UserActivity> findByTokenAndActioned(String token, boolean actioned);
}
