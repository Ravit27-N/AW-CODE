package com.allweb.rms.core.firebase;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.MulticastMessage.Builder;

/**
 * Represent the Firebase {@link MulticastMessage.Builder} configurer.
 *
 * <p>This configurer allow chains configuration by providing a {@link MulticastMessage.Builder}
 * through {@link #getConfiguredMulticastMessageBuilder()}.
 *
 * <p>Specific configuration class should provide the configuration through overriding the {@link
 * AbstractFCMMessageConfigurer#configure(com.google.firebase.messaging.Message.Builder)}.
 */
public interface FCMMulticastMessageConfigurer {

  /**
   * Get the {@link MulticastMessage} builder for current configuration.
   *
   * <p>To get the configured message builder, call {@link #configure()} first.
   *
   * @return {@link MulticastMessage.Builder}
   */
  Builder getConfiguredMulticastMessageBuilder();

  /**
   * The {@link MulticastMessage.Builder} configuration operation.
   */
  void configure();
}
