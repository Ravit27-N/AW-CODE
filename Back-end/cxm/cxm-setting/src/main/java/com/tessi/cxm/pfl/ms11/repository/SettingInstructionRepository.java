package com.tessi.cxm.pfl.ms11.repository;

import com.tessi.cxm.pfl.ms11.entity.SettingInstruction;
import com.tessi.cxm.pfl.ms11.entity.projection.BatchSettingProjection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingInstructionRepository extends JpaRepository<SettingInstruction, Long> {

  @Query(
      "select sd.channel as channel, sd.subChannel as subChannel, sd.modelType as modelType "
          + "from SettingInstruction sd inner join Setting s on sd.setting.id = s.id where "
          + "(s.idCreator = :idCreator "
          + "and lower(s.flowType) = lower(:flowType)) "
          + "or lower(sd.flowName) = lower(:flowName)")
  Optional<BatchSettingProjection> extractBatchSetting(
      @Param("flowType") String flowType,
      @Param("idCreator") long idCreator,
      @Param("flowName") String flowName);

  @Query(
      "select sd from SettingInstruction sd "
          + "where (sd.setting.idCreator = :idCreator "
          + "and lower(sd.setting.flowType) = lower(:flowType))"
          + " or lower(sd.template) = lower(:template) ")
  SettingInstruction getInstructionDetails(
      @Param("flowType") String flowType,
      @Param("template") String template,
      @Param("idCreator") long idCreator);

  @Query(
      "select sd from SettingInstruction sd "
          + "where sd.setting.idCreator = :idCreator "
          + "and lower(sd.setting.flowType) = lower(:flowType)")
  SettingInstruction getLastInstructionDetailsWithoutTemplate(
      @Param("flowType") String flowType, @Param("idCreator") long idCreator);
}
