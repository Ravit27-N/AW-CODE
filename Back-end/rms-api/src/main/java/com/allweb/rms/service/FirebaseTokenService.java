package com.allweb.rms.service;

import com.allweb.rms.entity.jpa.FirebaseToken;
import com.allweb.rms.entity.jpa.FirebaseTokenId;
import com.allweb.rms.repository.jpa.FirebaseTokenRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FirebaseTokenService {
  private final FirebaseTokenRepository firebaseTokenRepository;

  public FirebaseTokenService(FirebaseTokenRepository userRepository) {
    this.firebaseTokenRepository = userRepository;
  }

  public FirebaseToken saveUserToken(String userId, String deviceId, String fcmToken) {
    FirebaseToken firebaseToken;
    Optional<FirebaseToken> updatingUser =
        firebaseTokenRepository.findById(new FirebaseTokenId(userId, deviceId));
    if (updatingUser.isPresent()) {
      firebaseToken = updatingUser.get();
      firebaseToken.setDeviceId(deviceId);
      firebaseToken.setFcmToken(fcmToken);
    } else {
      firebaseToken = new FirebaseToken(userId, deviceId, fcmToken);
    }
    return firebaseTokenRepository.save(firebaseToken);
  }

  public List<FirebaseToken> removeAll(String userId, List<String> tokens) {
    List<FirebaseToken> firebaseTokens = firebaseTokenRepository.findByUserId(userId);
    List<FirebaseToken> deletingTokens =
        firebaseTokens.stream()
            .filter(firebaseToken -> tokens.contains(firebaseToken.getFcmToken()))
            .collect(Collectors.toList());
    if (!deletingTokens.isEmpty()) {
      firebaseTokenRepository.deleteAll(deletingTokens);
    }
    return new ArrayList<>();
  }

  public List<FirebaseToken> getAll(String userId) {
    return this.firebaseTokenRepository.findByUserId(userId);
  }
}
