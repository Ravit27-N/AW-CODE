package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.SystemConfiguration;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Integer> {

  Optional<SystemConfiguration> findByIdAndActiveIsTrue(int id);

  Optional<SystemConfiguration> findByConfigKeyAndActiveIsTrue(String configKey);

  Optional<SystemConfiguration> findSystemMailConfigurationByConfigKey(String configKey);

  int deleteByIdAndConfigKeyNotIn(int id, Collection<String> configKey);

  Optional<SystemConfiguration> findByIdAndConfigKeyNotIn(int id, Collection<String> configKey);

  List<SystemConfiguration> findAllByConfigKeyIn(Collection<String> configKey);

  Optional<SystemConfiguration> findSystemConfigurationByConfigKey(String configKey);

  @Query(
      "select s from SystemConfiguration s where lower(s.configKey) like %?1% or lower(s.configValue) like %?1% or lower(s.description) like %?1%")
  Page<SystemConfiguration> findAllByConfigKeyOrConfigValue(String filter, Pageable pageable);

  @Query(
      "select s from SystemConfiguration s where lower(s.configValue) like %?1% or lower(s.configValue) like %?1% or lower(s.description) like %?1%")
  List<SystemConfiguration> findAll(String filter, Pageable pageable);
}
