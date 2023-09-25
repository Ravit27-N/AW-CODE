package com.tessi.cxm.pfl.ms11.repository;

import com.tessi.cxm.pfl.ms11.entity.PortalSetting;
import com.tessi.cxm.pfl.ms11.entity.projection.PortalCampaignSettingProjection;
import com.tessi.cxm.pfl.ms11.entity.projection.PortalSettingProjection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalSettingRepository extends JpaRepository<PortalSetting, Long> {

  @Query(
      "select ps.configPath as configPath, ps.section as section "
          + "from Setting as s inner join PortalSetting ps on s.id = ps.id "
          + "where lower(s.flowType) = lower(:flowType)")
  Optional<PortalSettingProjection> getPortalSetting(@Param("flowType") String flowType);

  @Query(
      "select si.channel as channel, si.subChannel as subChannel "
          + "from Setting as s inner join s.settingInstruction si "
          + "where lower(s.flowType) = lower(:flowType)")
  Optional<PortalCampaignSettingProjection> getPortalCampaignSetting(
      @Param("flowType") String flowType);
}
