package com.tessi.cxm.pfl.ms3.dto;

import java.util.List;

public interface DocumentNotification {
  long getId();

  String getIdDoc();

  List<NotificationDoc> getNotifications();
}
