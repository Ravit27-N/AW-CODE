package com.allweb.rms.repository.jpa;

import com.allweb.rms.entity.jpa.FirebaseToken;
import com.allweb.rms.entity.jpa.FirebaseTokenId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, FirebaseTokenId> {

  List<FirebaseToken> findByUserId(String userId);
}
