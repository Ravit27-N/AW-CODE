package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  Optional<User> findByUserEntityIdAndDeleted(String uuid, Boolean deleted);

  Optional<User> findByEmailAndDeleted(String email, Boolean deleted);

  @Modifying
  @Query("update User set deleted = :deleted where id = :id")
  void updateDeleted(@Param("id") Long id, @Param("deleted") boolean deleted);

  Optional<User> findByIdAndDeleted(Long id, boolean deleted);

  @Query("select u.id from User u join u.roles r where r.name = :roleName and u.id in :employeeIds")
  List<Long> getUserByRole(
      @Param("roleName") String roleName, @Param("employeeIds") List<Long> employeeIds);

  @Modifying
  @Query("update User set email = :email where id = :id")
  void updateEmail(@Param("id") Long id, @Param("email") String email);

  @Modifying
  @Query("update User set active = :active where id = :id")
  void updateStatus(@Param("id") Long id, @Param("active") boolean active);
}
