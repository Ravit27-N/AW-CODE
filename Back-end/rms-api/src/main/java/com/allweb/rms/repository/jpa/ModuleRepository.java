package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.Module;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface ModuleRepository extends JpaRepository<Module, Integer> {

  @Query(
      value =
          "select id, created_at, created_by, last_modified_by, updated_at, active, description, name from module m "
              + "where case when ?1 = 'all' then ?1 = 'all' else name ilike '%'||?1||'%' or description ilike '%'||?1||'%' end ",
      nativeQuery = true)
  Page<Module> getAllModules(String filter, Pageable pageable);
}
