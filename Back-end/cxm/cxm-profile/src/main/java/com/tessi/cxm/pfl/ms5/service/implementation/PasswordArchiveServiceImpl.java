package com.tessi.cxm.pfl.ms5.service.implementation;

import com.tessi.cxm.pfl.ms5.entity.PasswordArchive;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.repository.PasswordArchiveRepository;
import com.tessi.cxm.pfl.ms5.service.specification.PasswordArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PasswordArchiveServiceImpl implements PasswordArchiveService {

    private final PasswordArchiveRepository passwordArchiveRepository;

    private final PasswordEncoder passwordEncoder;

    public PasswordArchiveServiceImpl(PasswordArchiveRepository passwordArchiveRepository, PasswordEncoder passwordEncoder) {
        this.passwordArchiveRepository = passwordArchiveRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Boolean isNotInPasswordArchive(UserEntity userEntity, String password) {
        log.info("PasswordArchiveService - Start of verification archive password process by username {}", userEntity.getUsername());

        List<PasswordArchive> passwordArchives = passwordArchiveRepository.findLastChangedPasswordByUserName(userEntity.getUsername(),
                PageRequest.of(0, 3));

        if(CollectionUtils.isNotEmpty(passwordArchives) && CollectionUtils.size(passwordArchives) >= 1) {
            Optional<PasswordArchive> passwordArchive = passwordArchives.stream().filter(
                    archive ->  passwordEncoder.matches(password, archive.getPassword())
            ).findAny();

            return passwordArchive.isPresent();
        }
        log.info("PasswordArchiveService - End of verification archive password process by username {}", userEntity.getUsername());
        return Boolean.FALSE;
    }
    @Override
    @Transactional
    public void addPasswordToArchive(UserEntity user, String password) {
        log.info("PasswordArchiveService - START of password archiving for {}", user.getUsername());
        PasswordArchive passwordArchive = PasswordArchive.builder()
                .userEntity(user)
                .changeDate(LocalDateTime.now())
                .password(passwordEncoder.encode(password))
                .build();
        passwordArchiveRepository.save(passwordArchive);
        log.info("PasswordArchiveService - END of password archiving for {}", user.getUsername());
    }
}
