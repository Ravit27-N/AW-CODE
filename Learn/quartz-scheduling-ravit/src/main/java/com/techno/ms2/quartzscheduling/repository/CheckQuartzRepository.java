package com.techno.ms2.quartzscheduling.repository;

import com.techno.ms2.quartzscheduling.entity.CheckQuartz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckQuartzRepository extends JpaRepository<CheckQuartz, Long> {}
