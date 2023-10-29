package com.techno.ms2.quartzscheduling.controller;

import com.techno.ms2.quartzscheduling.dto.CheckQuartzDto;
import com.techno.ms2.quartzscheduling.service.QuartzService;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/quartz-scheduling")
public class QuartzController {
  private final QuartzService quartzService;

  @PostMapping
  public void getQuartzScheduling(@RequestBody CheckQuartzDto checkQuartzDto) {
    quartzService.setScheduleUtility(checkQuartzDto);
  }
}
