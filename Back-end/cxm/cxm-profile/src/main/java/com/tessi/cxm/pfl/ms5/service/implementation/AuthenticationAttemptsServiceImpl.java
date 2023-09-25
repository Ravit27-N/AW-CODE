package com.tessi.cxm.pfl.ms5.service.implementation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.tessi.cxm.pfl.ms5.dto.request.AuthenticationAttemptsRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.request.UserLoginAttemptDTO;
import com.tessi.cxm.pfl.ms5.dto.response.AuthenticationAttemptsDTO;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.AuthenticationAttemptsService;
import com.tessi.cxm.pfl.ms5.service.specification.PasswordArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tessi.cxm.pfl.ms5.entity.AuthenticationAttempts;
import com.tessi.cxm.pfl.ms5.repository.AuthenticationAttemptsRepository;

@Service
@Slf4j
public class AuthenticationAttemptsServiceImpl implements AuthenticationAttemptsService {
    private final AuthenticationAttemptsRepository attemptsRepository;
    private final UserRepository userRepository;

    private final PasswordArchiveService passwordArchiveService;

    public AuthenticationAttemptsServiceImpl(AuthenticationAttemptsRepository attemptsRepository, UserRepository userRepository,
                                             PasswordArchiveService passwordArchiveService) {
        this.attemptsRepository = attemptsRepository;
        this.userRepository = userRepository;
        this.passwordArchiveService = passwordArchiveService;
    }
    @Override
    public AuthenticationAttemptsDTO isBlocked(AuthenticationAttemptsRequestDTO request) {
        log.info("start of account verification process if it is blocked by username {}", request.getUserName());
        Optional<UserEntity> userEntity = this.userRepository
                .findByUsernameAndIsActiveTrue(request.getUserName());

        if(userEntity.isEmpty()) {
            log.info("User is not found {}", request.getUserName());
            return buildLoginAttemptsResponse();
        }

        List<AuthenticationAttempts> attempts = attemptsRepository.findLastAttemptByUserName(request.getUserName(), PageRequest.of(0, 5),
                LocalDateTime.now().minusMinutes(30));

        long successAttemptsCount = findSuccessAttemptsCount(attempts);
        long currentCountOfRemainingAttempts = 5 - CollectionUtils.size(attempts) + successAttemptsCount;

        if(CollectionUtils.isNotEmpty(attempts) && CollectionUtils.size(attempts) >= 5) {
            log.info("start of account verification process if it is blocked by username {} - with five attempts found in less than 30 min", request.getUserName());
            if (successAttemptsCount >= 1) {
                log.info("END of account verification process if it is blocked by username {} - with less than five unsuccessful attempts found in less than 30 min", request.getUserName());
                return buildLoginAttemptsResponse(userEntity.get(), currentCountOfRemainingAttempts);
            }

            return buildLoginAttemptsResponse(attempts.stream().findFirst().get().getAttemptDate());
        }

        log.info("END of account verification process if it is blocked by username {} - with less than five unsuccessful attempts found in less than 30 min", request.getUserName());

        return buildLoginAttemptsResponse(userEntity.get(), currentCountOfRemainingAttempts);
    }

    @Transactional
    @Override
    public UserLoginAttemptDTO addUserLoginAttempt(UserLoginAttemptDTO userLoginAttempt) {
        log.info("START saving account login attempts {}", userLoginAttempt.getUserName());
        Optional<UserEntity> userEntity = this.userRepository
                .findByUsernameAndIsActiveTrue(userLoginAttempt.getUserName());

        if(userEntity.isEmpty())
            log.info("User is not found {}", userLoginAttempt.getUserName());
        else {
            if (userLoginAttempt.getLoginStatus().equals(Boolean.FALSE) || (userLoginAttempt.getLoginStatus().equals(Boolean.TRUE)
                    && StringUtils.isBlank(userLoginAttempt.getPassword()) )) {

                AuthenticationAttempts attempt = AuthenticationAttempts.builder()
                        .attemptDate(LocalDateTime.now())
                        .userEntity(userEntity.get())
                        .isFailedAttempt(userLoginAttempt.getLoginStatus())
                        .build();
                attemptsRepository.save(attempt);
                log.info("END of saving account login attempts {}", userLoginAttempt.getUserName());
            }

        }

        if( StringUtils.isNotBlank(userLoginAttempt.getPassword())) {
            if (passwordArchiveService.isNotInPasswordArchive(userEntity.get(), userLoginAttempt.getPassword()).equals(Boolean.FALSE))
                passwordArchiveService.addPasswordToArchive(userEntity.get(), userLoginAttempt.getPassword());
        }

        return userLoginAttempt;
    }
    private AuthenticationAttemptsDTO buildLoginAttemptsResponse() {
        return AuthenticationAttemptsDTO.builder().isBlocked(false).forceToChangePassword(false).build();
    }

    private AuthenticationAttemptsDTO buildLoginAttemptsResponse(UserEntity userEntity, long currentCountOfRemainingAttempts) {
        return AuthenticationAttemptsDTO.builder().isBlocked(false)
                .forceToChangePassword(checkIfAuthorizedToAuthenticate(userEntity).isEmpty())
                .currentCountOfRemainingAttempts(currentCountOfRemainingAttempts).build();
    }

    private AuthenticationAttemptsDTO buildLoginAttemptsResponse(LocalDateTime lastFailedAttempt) {
        return AuthenticationAttemptsDTO.builder().isBlocked(true).forceToChangePassword(false)
                .minutesRemaining(Duration.between(LocalDateTime.now(), lastFailedAttempt).toMinutes()+30).build();
    }

    private long findSuccessAttemptsCount(List<AuthenticationAttempts> attempts) {
        return attempts.stream().filter(
                authenticationAttempt ->  Boolean.FALSE.equals(authenticationAttempt.isFailedAttempt())
        ).count();
    }

    private Optional<AuthenticationAttempts> checkIfAuthorizedToAuthenticate(UserEntity userEntity) {
        return attemptsRepository.findFirstByUserEntityAndIsFailedAttemptFalseAndAttemptDateGreaterThanOrderByAttemptDateDesc(userEntity, LocalDateTime.now().minusDays(160));
    }
    

}
