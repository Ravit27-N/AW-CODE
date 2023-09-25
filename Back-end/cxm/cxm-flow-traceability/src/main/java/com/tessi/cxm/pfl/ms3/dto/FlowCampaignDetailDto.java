package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowCampaignDetailDto implements Serializable {

  private String campaignName;
  private String htmlTemplate;

  private String campaignType;
  private long totalRecord;
  private long totalError;
  private long totalDelivered;
  private long totalOpened;
  private long totalClicked;
  private long totalBounce;
  private long totalBlock;
  private long temporaryError;
  private long permanentError;
  private long totalResent;
  private long totalCanceled;

  public String getCampaignName() {
    if (this.campaignName == null) {
      return "";
    }
    return this.campaignName;
  }

  public String getHtmlTemplate() {
    if (this.htmlTemplate == null) {
      return "";
    }
    return this.htmlTemplate;
  }

  public String getCampaignType() {
    if (this.campaignType == null) {
      return "";
    }
    return this.campaignType;
  }

  public double getDeliveredPercentage() {
    if (this.totalDelivered == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalDelivered) / this.totalRecord) * 100;
  }

  public double getErrorPercentage() {
    if (this.totalError == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalError) / this.totalRecord) * 100;
  }

  public double getOpenedPercentage() {
    if (this.totalOpened == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalOpened) / this.totalRecord) * 100;
  }

  public double getClickedPercentage() {
    if (this.totalClicked == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalClicked) / this.totalRecord) * 100;
  }

  public double getBouncePercentage() {
    if (this.totalBounce == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalBounce) / this.totalRecord) * 100;
  }

  public double getBlockPercentage() {
    if (this.totalBlock == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalBlock) / this.totalRecord) * 100;
  }

  public double getTemporaryErrorPercentage() {
    if (this.temporaryError == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.temporaryError) / this.totalRecord) * 100;
  }

  public double getPermanentErrorPercentage() {
    if (this.totalError != 0) {
      this.permanentError = permanentError + totalError;
    }
    if (this.permanentError == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.permanentError) / this.totalRecord) * 100;
  }

  public double getBlockedPercentage() {
    if (this.totalBlock == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalBlock) / this.totalRecord) * 100;
  }

  public double getResentPercentage() {
    if (this.totalResent == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalResent) / this.totalRecord) * 100;
  }

  public double getCanceledPercentage() {
    if (this.totalCanceled == 0 || this.totalRecord == 0) {
      return 0;
    }
    return (((double) this.totalCanceled) / this.totalRecord) * 100;
  }
}
