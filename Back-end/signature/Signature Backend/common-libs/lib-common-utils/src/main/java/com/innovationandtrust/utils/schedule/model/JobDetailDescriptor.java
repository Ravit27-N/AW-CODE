package com.innovationandtrust.utils.schedule.model;

import com.innovationandtrust.utils.schedule.job.JobData;
import java.io.Serializable;
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
public class JobDetailDescriptor implements JobData, Serializable {

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
    if (!this.getJobDataMap().isEmpty()) {
      jobBuilder = jobBuilder.setJobData(this.getJobDataMap());
    }
    return jobBuilder.build();
  }

  @Override
  public JobDataMap getJobDataMap() {
    if (this.data != null) {
      return data.getJobDataMap();
    }
    return new JobDataMap();
  }

  /**
   * The unique identifier a JobDetail specified byt this descriptor.
   *
   * <p>The return {@link JobKey} is a combination of the group name and the job name.
   *
   * @return {@link JobKey}
   */
  public JobKey getJobKey() {
    return new JobKey(this.id, this.groupName);
  }

  /**
   * Whether the Job should remain stored after it is orphaned (no Triggers point to it).
   *
   * <p>If not explicitly set, the default value is false.
   *
   * @param isPermanent refers to the flag to indicate the state of permanent persistance data into
   *     a database
   */
  public void isPersistPermanently(boolean isPermanent) {
    this.storeDurably = isPermanent;
  }

  public boolean isPersistPermanently() {
    return this.storeDurably;
  }
}
