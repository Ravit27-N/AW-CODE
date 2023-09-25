package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.dto.UserCreatedByProjection;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserDetails;
import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserPrivilegeDetails;
import com.tessi.cxm.pfl.ms5.entity.projection.UserInfoProjection;
import com.tessi.cxm.pfl.shared.service.SharedRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * This repository is UserServiceRepository if we are looking in specs or in terms of business
 * requirement.
 *
 * @author Sokhour LACH
 * @author Piseth KHON
 * @author Vichet CHANN
 */
@Repository
public interface UserRepository
    extends JpaRepository<UserEntity, Long>, SharedRepository<Long>,
    JpaSpecificationExecutor<UserEntity> {

  @Query("select us.username as username, us.id as id from UserEntity us where us.department.id = :serviceId")
  List<LoadUserPrivilegeDetails> getAllUsersInService(@Param("serviceId") long serviceId);

  @Query(
      "select us.username as username, us.id as id from UserEntity us inner join Department s on s.id = us.department.id "
          + "inner join Division d on d.id = s.division.id where d.id = :divisionId")
  List<LoadUserPrivilegeDetails> getAllUsersInDivision(@Param("divisionId") long divisionId);

  @Query(
      "select s.id as serviceId, d.id as divisionId, c.id as clientId from UserEntity us "
          + " inner join Department s on s.id = us.department.id inner join Division d "
          + " on d.id = s.division.id inner join Client c on c.id = d.client.id"
          + " where us.technicalRef = :userId and us.isActive = true")
  Optional<LoadOrganization> loadOrganizationUser(@Param("userId") String userId);

  /**
   * To load all users info base on client id {@link Client#getId()}.
   *
   * @param clientId refer to client id {@link Client#getId()}
   * @param isActive refer to the status of the user. If isActive is null, it will respond with all
   *     user information, both true and false.
   * @return collection {@link List} of {@link LoadUserPrivilegeDetails}.
   */
  @Query(
      "SELECT DISTINCT us.username AS username, us.id AS id FROM UserEntity us "
          + "INNER JOIN Department s ON s.id = us.department.id "
          + "INNER JOIN Division d ON d.id = s.division.id "
          + "INNER JOIN Client c ON c.id = d.client.id "
          + "WHERE c.id = :clientId AND (:isActive IS NULL OR us.isActive = :isActive)")
  List<LoadUserPrivilegeDetails> getAllUsersInClient(
      @Param("clientId") long clientId, @Param("isActive") Boolean isActive);

  boolean existsByUsernameAndIsActiveTrue(String username);

  List<UserEntity> findAllByIdInAndIsActiveTrue(Collection<Long> id);

  @EntityGraph(value = "UserInfo", type = EntityGraphType.FETCH)
  Optional<UserEntity> findByIdAndIsActiveTrue(Long id);

  @EntityGraph(value = "UserInfo", type = EntityGraphType.FETCH)
  Optional<UserEntity> findByTechnicalRefAndIsActiveTrue(String technicalRef);
  Optional<List<UserEntity>> findAllByDepartmentIdAndTechnicalRefAndIsActiveTrue(
      long serviceId, String technicalRef);
  Optional<UserEntity> findByUsernameAndIsActiveTrue(String username);
  @Query(
      "select u.department.id as serviceId, "
          + "u.department.name as serviceName, "
          + "u.username as username, "
          + "concat(u.firstName, ' ', u.lastName) as fullName , "
          + "u.id as userId, u.department.division.client.name as customer from UserEntity u "
          + "where u.id = :id and u.isActive = true")
  Optional<UserInfoProjection> findUserInfoById(@Param("id") long id);

  @Query(
      "select u.department.id as serviceId, "
          + "u.department.name as serviceName, "
          + "u.username as username, "
          + "concat(u.firstName, ' ', u.lastName) as fullName , "
          + "u.technicalRef as userId, u.department.division.client.name as customer from UserEntity u "
          + "where u.technicalRef = :technicalRef and u.isActive = true")
  Optional<UserInfoProjection> findUserInfoByTechnicalRef(@Param("technicalRef") String technicalRef);

  @Query(
      "select u.department.id as serviceId, "
          + "u.department.name as serviceName, "
          + "u.username as username, "
          + "concat(u.firstName, ' ', u.lastName) as fullName , "
          + "u.id as userId, u.department.division.client.name as customer from UserEntity u "
          + "where u.username = :username and u.isActive = true ")
  Optional<UserInfoProjection> findUserInfoByUsername(@Param("username") String username);

  @Override
  @EntityGraph(value = "UserEntity", type = EntityGraphType.FETCH)
  Page<UserEntity> findAll(Specification<UserEntity> specs, Pageable pageable);

  @Query(
          "select us.username as username, us.id as id from UserEntity us inner join Department s "
                  + "on s.id = us.department.id inner join Division d on d.id = s.division.id "
                  + "inner join Client c on c.id = d.client.id where c.id = :clientId")
  List<LoadUserPrivilegeDetails> getAllUsersIdInClient(@Param("clientId") long clientId);

  @Query(
          "select us.username as username, us.id as id from UserEntity us inner join Department s on s.id = us.department.id "
                  + "inner join Division d on d.id = s.division.id where d.id = :divisionId")
  List<LoadUserPrivilegeDetails> getAllUsersIdInDivision(@Param("divisionId") long divisionId);

  @Query("select us.username as username, us.id as id from UserEntity us where us.department.id = :serviceId")
  List<LoadUserPrivilegeDetails> getAllUsersIdInService(@Param("serviceId") long serviceId);

  @Query(
      "select s.id as serviceId, d.id as divisionId, c.id as clientId from UserEntity us "
          + " inner join Department s on s.id = us.department.id inner join Division d "
          + " on d.id = s.division.id inner join Client c on c.id = d.client.id"
          + " where us.id = :userId and us.isActive = true")
  Optional<LoadOrganization> loadUserOrganization(@Param("userId") Long userId);

  List<UserCreatedByProjection> findByIsAdminTrueAndIsActiveTrue();


  @Query(
          "select us.username as username, us.id as id, s.name as serviceName, d.name as divisionName, c.name as clientName, "
                  + "s.id as serviceId, d.id as divisionId, c.id as clientId  from UserEntity us inner join Department s "
                  + "on s.id = us.department.id inner join Division d on d.id = s.division.id "
                  + "inner join Client c on c.id = d.client.id where c.id = :clientId")
  List<LoadUserDetails> loadAllUsersDetailsIdInClient(@Param("clientId") long clientId);

  @Query(
          "select us.username as username, us.id as id, s.name as serviceName, d.name as divisionName, d.client.name as clientName, "
                  + "s.id as serviceId, d.id as divisionId, d.client.id as clientId "
                  + "from UserEntity us inner join Department s on s.id = us.department.id "
                  + "inner join Division d on d.id = s.division.id where d.id = :divisionId")
  List<LoadUserDetails> loadAllUsersDetailsIdInDivision(@Param("divisionId") long divisionId);

  @Query(
          "select us.username as username, us.id as id, us.department.name as serviceName, "
                  + "us.department.division.name as divisionName,us.department.division.client.name as clientName, "
                  + "us.department.id as serviceId, us.department.division.id as divisionId, us.department.division.client.id as clientId "
                  + "from UserEntity us where us.department.id = :serviceId")
  List<LoadUserDetails> loadAllUsersDetailsIdInService(@Param("serviceId") long serviceId);
}
