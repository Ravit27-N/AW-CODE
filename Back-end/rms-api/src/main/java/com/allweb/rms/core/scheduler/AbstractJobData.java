package com.allweb.rms.core.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.quartz.JobDataMap;

public abstract class AbstractJobData implements JobData, JobDataValidation {

  private final Map<String, Object> dataMap;
  private final ErrorMessage errorMessage = new ErrorMessage();

  protected AbstractJobData() {
    this.dataMap = new HashMap<>();
  }

  protected AbstractJobData(JobData jobData) {
    this.dataMap = jobData.getJobDataMap();
  }

  protected AbstractJobData(JobDataMap jobDataMap) {
    this.dataMap = jobDataMap.getWrappedMap();
  }

  @Override
  public JobDataMap getJobDataMap() {
    this.validate(errorMessage);
    return this.getJobDataMapInternal();
  }

  @Override
  public void validate(ErrorMessage errorMessage) {
    this.validateInternal(errorMessage);
    if (!this.errorMessage.isEmpty()) {
      throw new JobDataValidationException(errorMessage.toString());
    }
  }

  protected JobDataMap getJobDataMapInternal() {
    return new JobDataMap(this.dataMap);
  }

  protected abstract void validateInternal(ErrorMessage errorMessage);

  protected <T> T getData(String key, Class<? extends T> clazz, T defaultValue) {
    if (!dataMap.containsKey(key)) {
      return defaultValue;
    }
    try {
      return getData(key, clazz);
    } catch (ClassCastException e) {
      return defaultValue;
    }
  }

  protected <T> T getData(String key, Class<? extends T> clazz) {
    Object data = dataMap.get(key);
    return clazz.cast(data);
  }

  protected Object getData(String key) {
    return dataMap.get(key);
  }

  protected void putData(String key, Object data) {
    this.dataMap.put(key, data);
  }

  protected boolean containsKey(String key) {
    return dataMap.containsKey(key);
  }
}
