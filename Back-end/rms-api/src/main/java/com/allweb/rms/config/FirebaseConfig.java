package com.allweb.rms.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class FirebaseConfig {

  @Value("${rms.firebase.database-url}")
  private String databaseUrl;

  @Bean
  public FirebaseApp firebase(
      @Value("${rms.firebase.service-account}") Resource firebaseServiceAccount)
      throws IOException {
    FirebaseOptions options =
        FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(firebaseServiceAccount.getInputStream()))
            .setDatabaseUrl(databaseUrl)
            .build();
    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(options);
    }
    return FirebaseApp.getInstance();
  }
}
