package com.allweb.rms.core.firebase;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.MulticastMessage.Builder;

public abstract class AbstractFCMMulticastMessageConfigurer
    implements FCMMulticastMessageConfigurer {

  private final Builder messageBuilder;

  protected AbstractFCMMulticastMessageConfigurer() {
    this.messageBuilder = MulticastMessage.builder();
  }

  protected AbstractFCMMulticastMessageConfigurer(FCMMulticastMessageConfigurer fcmMessageBuilder) {
    this.messageBuilder = fcmMessageBuilder.getConfiguredMulticastMessageBuilder();
  }

  @Override
  public Builder getConfiguredMulticastMessageBuilder() {
    this.configure();
    return this.messageBuilder;
  }

  @Override
  public void configure() {
    this.configure(this.messageBuilder);
  }

  /**
   * Configure the current {@link MulticastMessage.Builder} with the Firebase plateform-specified or
   * common information.
   *
   * @param messageBuilder current {@link MulticastMessage.Builder}.
   */
  protected abstract void configure(MulticastMessage.Builder messageBuilder);
}
