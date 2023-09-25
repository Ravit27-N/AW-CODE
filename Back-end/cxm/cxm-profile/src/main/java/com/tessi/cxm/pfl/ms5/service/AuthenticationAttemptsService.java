package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.dto.request.AuthenticationAttemptsRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.request.UserLoginAttemptDTO;
import com.tessi.cxm.pfl.ms5.dto.response.AuthenticationAttemptsDTO;

public interface AuthenticationAttemptsService {

    AuthenticationAttemptsDTO isBlocked(AuthenticationAttemptsRequestDTO request);
    UserLoginAttemptDTO addUserLoginAttempt(UserLoginAttemptDTO userLoginAttempt);
}
