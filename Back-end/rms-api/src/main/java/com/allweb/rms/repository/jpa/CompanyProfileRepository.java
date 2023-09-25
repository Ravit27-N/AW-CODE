package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.CompanyProfile;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Integer> {}
