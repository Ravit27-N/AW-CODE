package com.tessi.cxm.pfl.ms5.repository;

import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tessi.cxm.pfl.ms5.entity.AuthenticationAttempts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthenticationAttemptsRepository extends JpaRepository<AuthenticationAttempts, Long> ,
        JpaSpecificationExecutor<AuthenticationAttempts> {

    @Query("SELECT authAttempt FROM AuthenticationAttempts authAttempt " +
            "WHERE authAttempt.userEntity.username = :userName " +
            "AND authAttempt.attemptDate BETWEEN :dateNowMinusThirtyMinutes AND CURRENT_TIMESTAMP " +
            "ORDER BY authAttempt.attemptDate DESC")
    List<AuthenticationAttempts> findLastAttemptByUserName(@Param("userName") String userName, Pageable pageable,
                                                           @Param("dateNowMinusThirtyMinutes") LocalDateTime dateNowMinusThirtyMinutes);

    Optional<AuthenticationAttempts> findFirstByUserEntityAndIsFailedAttemptFalseAndAttemptDateGreaterThanOrderByAttemptDateDesc(UserEntity userEntity, LocalDateTime dateNowMinusOneHundredSixtyDays);

    Optional<AuthenticationAttempts> findFirstByUserEntityOrderByAttemptDateDesc(UserEntity userEntity);    
}