package com.techno.ms2.quartzscheduling.payload;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ChangeStatusLog {

    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;

    public ChangeStatusLog(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ChangeStatusLog(boolean success, String jobId, String jobGroup, String message) {
        this.success = success;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.message = message;
    }
}
