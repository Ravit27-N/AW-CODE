package com.innovationandtrust.utils.schedule.job;

import java.io.Serializable;
import org.quartz.JobDataMap;

public interface JobData extends Serializable {

  JobDataMap getJobDataMap();
}
