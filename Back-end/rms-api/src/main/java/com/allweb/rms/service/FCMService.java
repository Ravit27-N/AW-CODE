package com.allweb.rms.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.stereotype.Service;

@Service
public class FCMService {
  protected final FirebaseMessaging firebaseMessaging;

  protected FCMService(FirebaseApp firebaseApp) {
    this.firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
  }

  public String sendMessage(Message message) throws FirebaseMessagingException {
    return this.firebaseMessaging.send(message);
  }

  public BatchResponse sendMulticastMessage(MulticastMessage multicastMessage)
      throws FirebaseMessagingException {
    return this.firebaseMessaging.sendMulticast(multicastMessage);
  }
}
