package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface FlowCampaignDetailRepository
    extends JpaRepository<FlowCampaignDetail, Long>,
    JpaSpecificationExecutor<FlowCampaignDetail>
{

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalDelivered = totalDelivered + :sentSuccess where id = :id")
  void updateTotalDeliveredById(@Param("id") long id, @Param("sentSuccess") long sentSuccess);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalError = totalError + :sentError where id = :id")
  void updateTotalErrorById(@Param("id") long id, @Param("sentError") long sentError);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalError = totalError + :sentError where campaignId = :campaignId")
  void updateTotalErrorByCampaignId(@Param("campaignId") long campaignId,
      @Param("sentError") long sentError);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalBounce = totalBounce + :bounce where id = :id")
  void updateTotalBounceById(@Param("id") long id, @Param("bounce") long bounce);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalClicked = totalClicked + :clicked where id = :id")
  void updateTotalClickedById(@Param("id") long id, @Param("clicked") long clicked);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalOpened = totalOpened + :opened where  id = :id")
  void updateTotalOpenedById(@Param("id") long id, @Param("opened") long opened);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalBlock = totalBlock + :blocked where id = :id")
  void updateTotalBlockedById(@Param("id") long id, @Param("blocked") long blocked);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET temporaryError = temporaryError + :temporaryError where id = :id")
  void updateTotalTemporaryErrorById(@Param("id") long id,
      @Param("temporaryError") long temporaryError);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET permanentError = permanentError + :permanentError where id = :id")
  void updateTotalPermanentErrorById(@Param("id") long id,
      @Param("permanentError") long permanentError);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalResent = totalResent + :totalResent where id = :id")
  void updateTotalResentById(@Param("id") long id, @Param("totalResent") long totalResent);

  @Modifying
  @Query(
      "Update FlowCampaignDetail SET totalCanceled = totalCanceled + :totalCanceled where id = :id")
  void updateTotalCanceledById(@Param("id") long id, @Param("totalCanceled") long totalCanceled);

  @Query(
      "select (cd.totalError + cd.totalDelivered + cd.totalBlock + cd.totalBounce + cd.totalCanceled = cd.totalRecord) as isCompleted from FlowCampaignDetail cd where cd.campaignId = :campaignId")
  boolean isFlowCampaignCompleted(@Param("campaignId") long campaignId);

  @Query(
      "select (cd.totalError + cd.totalDelivered + cd.totalBlock + cd.totalBounce + cd.totalCanceled = cd.totalRecord) as isCompleted from FlowCampaignDetail cd where cd.id = :flowId")
  boolean isFlowCampaignCompletedByFlowId(@Param("flowId") long flowId);

  @Query(
      "select (cd.totalError = cd.totalRecord) as isError from FlowCampaignDetail cd where cd.campaignId = :campaignId")
  boolean isFlowCampaignError(@Param("campaignId") long campaignId);

  @Query(
          "select (cd.totalError = cd.totalRecord) as isError from FlowCampaignDetail cd where cd.id = :flowId")
  boolean isFlowCampaignErrorByFlowId(@Param("flowId") long flowId);

  @Modifying
  @Query(
          "Update FlowCampaignDetail SET totalRecord = :totalRecord where id = :id")
  void setTotalRecord(@Param("id") long id, @Param("totalRecord") long totalRecord);

  @Modifying
  @Query("UPDATE FlowCampaignDetail SET htmlTemplate = :htmlTemplate where id = :flowId")
  void updateFlowCampaignDetail(
      @Param("flowId") Long flowId, @Param("htmlTemplate") String htmlTemplate);
}
