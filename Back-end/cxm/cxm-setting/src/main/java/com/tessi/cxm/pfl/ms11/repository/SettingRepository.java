package com.tessi.cxm.pfl.ms11.repository;

import com.tessi.cxm.pfl.ms11.entity.PortalSetting;
import com.tessi.cxm.pfl.ms11.entity.Setting;
import com.tessi.cxm.pfl.ms11.entity.projection.DepositTypeProjection;
import com.tessi.cxm.pfl.ms11.entity.projection.DepositValidationProjection;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {

  @Query(
      "select s.idCreator as idCreator, s.flowType as flowType, s.scanActivation as scanActivation, (case when s.depositType = 'Batch' then true else s.scanActivation end) as configurationActivation  from Setting s where lower(s.customer) = lower(:customer) and lower(s.depositType) = lower(:depositType) and (s.connector is null or lower(s.connector) = lower(:connector)) and lower(s.extension) = lower(:extension)")
  DepositValidationProjection findDepositValidation(
      @Param("customer") String customer,
      @Param("depositType") String depositType,
      @Param("connector") String connector,
      @Param("extension") String extension);

  @Query(
      "select s.idCreator as idCreator, s.flowType as flowType, s.scanActivation as scanActivation, "
          + "(case when ps.isActive is null then false else ps.isActive end) as configurationActivation  "
          + "from Setting s left join PortalSetting ps on s.id = ps.setting.id "
          + "where lower(s.customer) = lower(:customer) and lower(s.flowType) = lower(:flowType)")
  DepositValidationProjection findDepositValidationByFlowType(
      @Param("customer") String customer, @Param("flowType") String depositType);

  @Query("SELECT DISTINCT st.depositType as depositType, st.scanActivation as scanActivation FROM Setting st WHERE st.customer = :customer")
  Optional<List<DepositTypeProjection>> findAllByCustomer(@Param("customer") String customer);

  @Query("SELECT st FROM Setting st WHERE st.customer = :customer AND st.depositType in (:depositTypes) AND st.extension in (:extensions)")
  List<Setting> findAllByCustomerAndDepositTypes(@Param("customer") String customer,
      @Param("depositTypes")
          Collection<String> depositTypes, @Param("extensions") Collection<String> extensions);

  List<Setting> findByCustomerInIgnoreCaseAndFlowTypeInIgnoreCase(
      List<String> customers, List<String> flowTypes);

  @Query("SELECT ps FROM Setting s INNER JOIN PortalSetting  ps ON s.id = ps.setting.id "
      + "WHERE s.customer = :customer AND s.depositType = :depositType AND s.extension = :extension")
  Optional<PortalSetting> findPortalSetting(@Param("customer") String customer,
      @Param("depositType") String depositType, @Param("extension") String extension);

  @Query("SELECT ps FROM Setting s INNER JOIN PortalSetting  ps ON s.id = ps.setting.id "
      + "WHERE s.customer = :customer AND s.depositType IN(:depositTypes) AND s.extension IN(:extensions)")
  Optional<List<PortalSetting>> findAllPortalSetting(@Param("customer") String customer,
      @Param("depositTypes") Collection<String> depositTypes,
      @Param("extensions") Collection<String> extensions);

  @Query("SELECT s FROM Setting s "
      + "WHERE s.customer = :customer AND s.depositType IN(:depositTypes) AND s.extension IN(:extensions)")
  Optional<List<Setting>> findAllSetting(@Param("customer") String customer,
      @Param("depositTypes") Collection<String> depositTypes,
      @Param("extensions") Collection<String> extensions);

  @Query("SELECT s FROM Setting s INNER JOIN PortalSetting ps ON s.id = ps.setting.id "
      + "WHERE s.customer = :customer AND s.depositType = :depositType AND s.extension = :extension")
  Optional<Setting> findSettingByCustomQuery(
      @Param("customer") String customer,
      @Param("depositType") String depositType,
      @Param("extension") String extension);
  Optional<Setting> findByFlowTypeIgnoreCaseAndScanActivationTrue(String flowType);
}
