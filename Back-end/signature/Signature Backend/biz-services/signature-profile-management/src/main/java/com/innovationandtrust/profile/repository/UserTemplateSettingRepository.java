package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.UserTemplateSetting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTemplateSettingRepository
    extends JpaRepository<UserTemplateSetting, Long>,
        JpaSpecificationExecutor<UserTemplateSetting> {

  Optional<UserTemplateSetting> findByUserIdAndTemplateId(Long userId, Long templateId);

  List<UserTemplateSetting> findByUserId(Long userId);
}
