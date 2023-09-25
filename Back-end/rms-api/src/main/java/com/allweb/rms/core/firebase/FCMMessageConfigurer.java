package com.allweb.rms.core.firebase;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Message.Builder;

/**
 * Represent the Firebase {@link Message.Builder} configurer.
 *
 * <p>This configurer allow chains configuration by providing a {@link Message.Builder} through
 * {@link #getConfiguredMessageBuilder()}.
 *
 * <p>Specific configuration class should provide the configuration through overriding the {@link
 * AbstractFCMMulticastMessageConfigurer#configure(com.google.firebase.messaging.MulticastMessage.Builder)}.
 */
public interface FCMMessageConfigurer {

  /**
   * Get the {@link Message} builder for current configuration.
   *
   * <p>To get the configured message builder, call {@link #configure()} first.
   *
   * @return {@link Message.Builder}
   */
  Builder getConfiguredMessageBuilder();

  /**
   * The {@link Message.Builder} configuration operation.
   */
  void configure();
}
