package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.ApplicationInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationInformationRepository
    extends JpaRepository<ApplicationInformation, Integer> {}
