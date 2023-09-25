package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.shared.model.DepartmentProjection;
import com.tessi.cxm.pfl.shared.repository.SpecificationExecutorWithProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

@Repository
public interface DepartmentRepository
    extends JpaRepository<Department, Long>,
        JpaSpecificationExecutor<Department>,
        SpecificationExecutorWithProjection<Department>,
        JpaSpecificationExecutorWithProjection<Department> {

        List<DepartmentProjection> findAllById(long id);

        // List<DepartmentProjection> findAllByDivisionId(long divisionId);
        List<DepartmentProjection> findAllByDivisionId(@Param("divisionId") long divisionId);

        @Query("select s from Department s "
                        + "inner join Division d on s.division.id = d.id "
                        + "inner join Client c on c.id = d.client.id "
                        + "where c.id = :clientId")
        List<DepartmentProjection> findAllByDivisionInClientId(@Param("clientId") long clientId);

        boolean existsAllByNameIgnoreCaseAndDivisionId(String name, long divisionId);

        @Query("select dp from Client as c "
                        + "inner join Division as dv on c.id = dv.client.id "
                        + "inner join Department as dp on dv.id = dp.division.id "
                        + "where c.id = :clientId ")
        List<Department> getAllByClient(@Param("clientId") long clientId);

        // add new
        @Query("select s from Department s ")
        List<DepartmentProjection> findAllByDivision();
}
