package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.UserRoleDetail;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@SecurityRequirement(name = "bearer")
public interface UserRoleDetailRepository extends JpaRepository<UserRoleDetail, Integer> {

  @Modifying
  @Query(value = "DELETE FROM user_role_detail WHERE user_role_id = ?1", nativeQuery = true)
  void deleteUserRoleDetailByUserRoleId(String userRoleId);

  long countAllByModuleId(int id);

  @Query(
      value =
          "SELECT cast(jsonb_agg(distinct jsonb_build_object('userRoleId',user_role_id,'moduleName', m.name,'moduleId', module_id,'deleteAble', is_delete_able ,'editAble', is_edit_able ,'insertAble', is_insert_able ,'viewAble', is_view_able))as json) "
              + "FROM user_role_detail u LEFT JOIN module m on u.module_id = m.id where user_role_id = ?1",
      nativeQuery = true)
  Object findByUserRoleId(String userRoleId);
}
