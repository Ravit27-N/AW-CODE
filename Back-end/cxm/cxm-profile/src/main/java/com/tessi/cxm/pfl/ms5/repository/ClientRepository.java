package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

@Repository
public interface ClientRepository
    extends JpaRepository<Client, Long>,
    JpaSpecificationExecutor<Client>,
    JpaSpecificationExecutorWithProjection<Client> {

  @Query(
      "select c.id from UserEntity us "
          + " inner join Department s on s.id = us.department.id inner join Division d "
          + " on d.id = s.division.id inner join Client c on c.id = d.client.id"
          + " where us.technicalRef = :userId and us.isActive = true ")
  Optional<Long> getClientIdByUserId(@Param("userId") String userId);

  @Query(
      "select count(c.id) from Client as c inner join Profile as p on c.id = p.client.id where c.id = :clientId")
  long getProfileCount(@Param("clientId") long clientId);

  @Query(
      "select c from Department s "
          + "inner join Division d on d.id = s.division.id "
          + "inner join Client c on c.id = d.client.id "
          + "where s.id = :serviceId"
  )
  Optional<Client> findClientByServiceId(@Param("serviceId") Long serviceId);

  boolean existsByNameIgnoreCase(String name);

  Optional<Client> findByName(String name);
  boolean existsByName(String name);

  @Query("select s from Client c "
      + "inner join Division d on c.id = d.client.id "
      + "inner join Department s on d.id = s.division.id "
      + "where lower(c.name) = lower(:clientName) and lower(d.name) = lower(:divisionName) and lower(s.name) = lower(:serviceName)")
  Optional<Department> findDepartment(@Param("clientName") String clientName,
      @Param("divisionName") String divisionName, @Param("serviceName") String serviceName);
}
