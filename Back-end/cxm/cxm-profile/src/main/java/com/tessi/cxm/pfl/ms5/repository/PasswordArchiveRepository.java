package com.tessi.cxm.pfl.ms5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tessi.cxm.pfl.ms5.entity.PasswordArchive;
import java.util.List;

@Repository
public interface PasswordArchiveRepository extends JpaRepository<PasswordArchive, Long>,
        JpaSpecificationExecutor<PasswordArchive> {

    @Query("SELECT passwordArchive FROM PasswordArchive passwordArchive " +
            "WHERE passwordArchive.userEntity.username = :userName " +
            "ORDER BY passwordArchive.changeDate DESC")
    List<PasswordArchive> findLastChangedPasswordByUserName(@Param("userName") String userName, Pageable pageable);
}
