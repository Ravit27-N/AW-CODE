package com.allweb.rms.core.firebase;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Message.Builder;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * The Firebase {@link Message} configurer for web push notifications.
 *
 * <p>This configurer can be used to configure the target topic and device token which the message
 * will be sent to.
 *
 * <p>Other configuration are notification title, body, icon and expire time for Firebase message
 * delivery.
 */
@Getter
@Setter
public class WebPushMessageConfigurer extends AbstractFCMMessageConfigurer {
  private static final String TIME_TO_LIVE_HEADER = "TTL";
  private String title;
  private String body;
  private String iconUrl;
  private String topic;
  private String deviceToken;
  private Long expiredTimeInSeconds = 0L;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Map<String, Object> customNotificationPayload = new HashMap<>();

  public WebPushMessageConfigurer() {
    super();
  }

  /**
   * Construct a new {@link WebPushMessageConfigurer} instance base on the other {@link
   * FCMMessageConfigurer}.
   *
   * <p>This configurer is intent to chains the configuration for the web-specified plateform of
   * Firebase messaging.
   *
   * @param builder base {@link FCMMessageConfigurer} for further configuration.
   */
  public WebPushMessageConfigurer(FCMMessageConfigurer builder) {
    super(builder);
  }

  @Override
  protected void configure(Builder messageBuilder) {
    messageBuilder
        .setTopic(this.topic)
        .setToken(this.deviceToken)
        .setWebpushConfig(
            WebpushConfig.builder()
                .putHeader(TIME_TO_LIVE_HEADER, this.expiredTimeInSeconds.toString())
                .setNotification(this.getNotification())
                .build());
  }

  private WebpushNotification getNotification() {
    return WebpushNotification.builder()
        .setTitle(this.title)
        .setBody(this.body)
        .setIcon(this.iconUrl)
        .putAllCustomData(this.customNotificationPayload)
        .build();
  }

  /**
   * Puts a custom key-value pair to the notification.
   *
   * @param key A non-null key.
   * @param value A non-null key.
   * @return
   */
  public Object putCustomNotificationData(String key, Object value) {
    return this.customNotificationPayload.put(key, value);
  }

  /**
   * Puts all the key-value pairs in the specified map to the notification.
   *
   * @param fields A non-null map. Map must not contain null keys or values.
   */
  public void putAllCustomNotificationDatas(Map<String, Object> fields) {
    this.customNotificationPayload.putAll(fields);
  }
}
