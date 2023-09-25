package com.tessi.cxm.pfl.ms11.repository;

import com.tessi.cxm.pfl.ms11.entity.CriteriaDistribution;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CriteriaDistributionRepository extends JpaRepository<CriteriaDistribution, Long> {

  @Query(
      "select cb from CriteriaDistribution cb where lower(cb.customer) = lower(:customer) and cb.name in :criteriaNames")
  List<CriteriaDistribution> findAllCriteria(
      @Param("customer") String customer, @Param("criteriaNames") List<String> criteriaNames);

  @Query(
      "select cb from CriteriaDistribution cb where lower(cb.customer) = lower(:customer) and cb.name = :criteriaName")
  Optional<CriteriaDistribution> findCriteria(
      @Param("customer") String customer, @Param("criteriaName") String criteriaName);

  List<CriteriaDistribution> findByCustomerIgnoreCaseAndNameInIgnoreCase(
      String customer, List<String> names);

  boolean existsByCustomerIgnoreCaseAndNameIgnoreCaseAndIsActiveTrue(String customer, String name);
}
