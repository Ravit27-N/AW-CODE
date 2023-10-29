package com.techno.ms2.quartzscheduling.service;

import com.techno.ms2.quartzscheduling.component.ScheduleUtility;
import com.techno.ms2.quartzscheduling.dto.CheckQuartzDto;
import com.techno.ms2.quartzscheduling.entity.CheckQuartz;
import com.techno.ms2.quartzscheduling.job.SimpleJob;
import com.techno.ms2.quartzscheduling.repository.CheckQuartzRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class QuartzService {
  private final ScheduleUtility scheduleUtility;
  private final CheckQuartzRepository checkQuartzRepository;
  private final ModelMapper modelMapper;

  public void setScheduleUtility(CheckQuartzDto checkQuartzDto) {
    var checkQuartz = modelMapper.map(checkQuartzDto, CheckQuartz.class);

    // save to database
    checkQuartzRepository.save(checkQuartz);

    // set the schedule
    scheduleUtility.schedule(checkQuartz);
  }




}
