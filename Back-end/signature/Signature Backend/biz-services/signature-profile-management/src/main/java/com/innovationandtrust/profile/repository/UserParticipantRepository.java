package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.UserParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserParticipantRepository
    extends JpaRepository<UserParticipant, Long>,
        JpaSpecificationExecutor<UserParticipant> {
    void deleteAllByTemplateIdAndUserId(Long templateId, Long userId);
}
