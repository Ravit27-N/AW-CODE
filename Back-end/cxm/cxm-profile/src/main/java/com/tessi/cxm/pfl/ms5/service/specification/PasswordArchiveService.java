package com.tessi.cxm.pfl.ms5.service.specification;

import com.tessi.cxm.pfl.ms5.entity.UserEntity;

public interface PasswordArchiveService {

    Boolean isNotInPasswordArchive(UserEntity userEntity, String password);
    void addPasswordToArchive(UserEntity user, String password);
}
