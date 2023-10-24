package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.UserTemplates;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTemplatesRepository
    extends JpaRepository<UserTemplates, Long>, JpaSpecificationExecutor<UserTemplates> {
  List<UserTemplates> findUserTemplatesByUserId(@Param("id") long id);

  Optional<UserTemplates> findByUserIdAndTemplateId(long userId, long templateId);

  Optional<UserTemplates> findByTemplateId(long templateId);
}
