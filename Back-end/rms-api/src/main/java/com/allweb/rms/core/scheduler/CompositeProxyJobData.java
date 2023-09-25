package com.allweb.rms.core.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.quartz.JobDataMap;

public class CompositeProxyJobData extends AbstractJobData {
  private final Map<String, AbstractJobData> delegates = new HashMap<>();

  public CompositeProxyJobData() {
    super();
  }

  @Override
  protected void validateInternal(ErrorMessage errorMessage) {
    for (Map.Entry<String, AbstractJobData> delegate : delegates.entrySet()) {
      delegate.getValue().validateInternal(errorMessage);
    }
  }

  @Override
  protected JobDataMap getJobDataMapInternal() {
    Map<String, Object> mergedDataMap = new HashMap<>();
    for (Map.Entry<String, AbstractJobData> delegate : delegates.entrySet()) {
      mergedDataMap.putAll(delegate.getValue().getJobDataMap().getWrappedMap());
    }
    return new JobDataMap(mergedDataMap);
  }

  public void addJobData(String key, AbstractJobData jobData) {
    this.delegates.put(key, jobData);
  }

  public AbstractJobData getJobData(String key) {
    return this.delegates.get(key);
  }

  public boolean containsJobData(String key) {
    return this.delegates.containsKey(key);
  }
}
