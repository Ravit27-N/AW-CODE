package com.allweb.rms.core.scheduler.model;

import com.allweb.rms.core.scheduler.JobData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;

@Setter
@RequiredArgsConstructor
public class JobDetailDescriptor {
  @Getter private final String id;

  @Getter private final String groupName;

  @Getter private final Class<? extends Job> jobClass;

  @Getter private String description;

  private boolean storeDurably = false;

  private JobData data;

  /**
   * Build a new {@link JobDetail} instance specified by this descriptor object.
   *
   * @return {@link JobDetail}
   */
  public JobDetail buildJobDetail() {
    JobBuilder jobBuilder =
        JobBuilder.newJob(this.getJobClass())
            .withIdentity(this.getId(), this.getGroupName())
            .storeDurably(this.storeDurably);
    if (this.getData() != null) {
      jobBuilder = jobBuilder.setJobData(this.getData());
    }
    return jobBuilder.build();
  }

  public JobDataMap getData() {
    if (this.data != null) {
      return data.getJobDataMap();
    }
    return null;
  }

  /**
   * The uniquely identifier a JobDetail specified byt this descriptor.
   *
   * <p>The return {@link JobKey} is a combination of the group name and the job name.
   *
   * @return {@link JobKey}
   */
  public JobKey getJobKey() {
    return new JobKey(this.id, this.groupName);
  }

  /**
   * Whether or not the Job should remain stored after it is orphaned (no Triggers point to it).
   *
   * <p>If not explicitly set, the default value is false.
   *
   * @param persist
   */
  public void persistInDatabasePermanently(boolean persist) {
    this.storeDurably = persist;
  }

  public boolean isPersistInDatabasePermanently() {
    return this.storeDurably;
  }
}
