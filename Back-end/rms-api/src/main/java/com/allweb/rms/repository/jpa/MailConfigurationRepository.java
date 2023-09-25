package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.MailConfiguration;
import com.allweb.rms.entity.jpa.MailConfigurationId;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface MailConfigurationRepository
    extends JpaRepository<MailConfiguration, MailConfigurationId> {

  Optional<MailConfiguration> findByIdAndDeletedIsFalse(int id);

  Optional<MailConfiguration> findByCandidateStatusId(int id);

  Optional<MailConfiguration> findById(int integer);

  Optional<MailConfiguration> findByCandidateStatusIdAndDeletedIsFalse(int id);
}
