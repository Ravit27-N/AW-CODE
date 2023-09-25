package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.UserRequestResetPassword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestResetPasswordRepository extends JpaRepository<UserRequestResetPassword, Long> {

  Optional<UserRequestResetPassword> findByToken(@Param("token") String token);
  boolean existsByEmail(@Param("email") String email);
  void deleteByEmail(@Param("email") String email);
}
