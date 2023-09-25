package com.allweb.rms.core.firebase;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.MulticastMessage.Builder;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * The Firebase {@link MulticastMessage} configurer for web push notifications.
 *
 * <p>This configurer can be used to configure the list of target device tokens which the message
 * will be sent to.
 *
 * <p>Other configuration are notification title, body, icon and expire time for Firebase message
 * delivery.
 */
@Getter
@Setter
public class WebPushMulticastMessageConfigurer extends AbstractFCMMulticastMessageConfigurer {
  private static final String TIME_TO_LIVE_HEADER = "TTL";

  private String title;
  private String body;
  private String iconUrl;
  private Long expiredTimeInSeconds = 0L;

  @Setter(AccessLevel.NONE)
  private Set<String> deviceTokens = new HashSet<>();

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Map<String, Object> customNotificationPayload = new HashMap<>();

  public WebPushMulticastMessageConfigurer() {
    super();
  }

  /**
   * Construct a new {@link WebPushMulticastMessageConfigurer} instance base on the other {@link
   * FCMMulticastMessageConfigurer}.
   *
   * <p>This configurer is intent to chains the configuration for the web-specified plateform of
   * Firebase messaging.
   *
   * @param builder base {@link FCMMulticastMessageConfigurer} for further configuration.
   */
  public WebPushMulticastMessageConfigurer(FCMMulticastMessageConfigurer builder) {
    super(builder);
  }

  @Override
  protected void configure(Builder messageBuilder) {
    messageBuilder
        .addAllTokens(this.deviceTokens)
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

  /**
   * Add more client device Token.
   *
   * @param deviceToken
   */
  public void addDeviceToken(String deviceToken) {
    this.deviceTokens.add(deviceToken);
  }

  /**
   * Add more client device Tokens.
   *
   * @param deviceTokens List of none duplicated client device tokens.
   */
  public void addDeviceTokens(Set<String> deviceTokens) {
    deviceTokens.stream()
        .filter(deviceToken -> this.deviceTokens.contains(deviceToken))
        .forEach(noneExistedtoken -> this.deviceTokens.add(noneExistedtoken));
  }
}
