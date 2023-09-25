package com.allweb.rms.core.firebase;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

/**
 * Helper class use to create {@link Message} or {@link MulticastMessage} by a provided {@link
 * FCMMessageConfigurer}
 *
 * @see {@link WebPushMessageConfigurer}
 * @see {@link WebPushMulticastMessageConfigurer}
 */
public final class FCMUtils {

  private FCMUtils() {}

  /**
   * Create a new Firebase {@link Message} using the provided {@code fcmMessageConfigurator}.
   *
   * @param fcmMessageConfigurer {@link FCMMessageConfigurer}.
   */
  public static Message createMessage(FCMMessageConfigurer fcmMessageConfigurer) {
    Message.Builder messageBuilder = fcmMessageConfigurer.getConfiguredMessageBuilder();
    return messageBuilder.build();
  }

  /**
   * Create a new Firebase {@link MulticastMessage} using the provided {@code
   * fcmMulticastMessageConfigurer}.
   *
   * @param fcmMulticastMessageConfigurer {@link FCMMulticastMessageConfigurer}.
   * @return object of {@link MulticastMessage}
   */
  public static MulticastMessage createMulticastMessage(
      FCMMulticastMessageConfigurer fcmMulticastMessageConfigurer) {
    MulticastMessage.Builder messageBuilder =
        fcmMulticastMessageConfigurer.getConfiguredMulticastMessageBuilder();
    return messageBuilder.build();
  }
}
