package com.allweb.rms.core.firebase;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Message.Builder;

public abstract class AbstractFCMMessageConfigurer implements FCMMessageConfigurer {

  private final Builder messageBuilder;

  protected AbstractFCMMessageConfigurer() {
    this.messageBuilder = Message.builder();
  }

  protected AbstractFCMMessageConfigurer(FCMMessageConfigurer fcmMessageBuilder) {
    this.messageBuilder = fcmMessageBuilder.getConfiguredMessageBuilder();
  }

  @Override
  public Builder getConfiguredMessageBuilder() {
    this.configure();
    return this.messageBuilder;
  }

  @Override
  public void configure() {
    this.configure(this.messageBuilder);
  }

  /**
   * Configure the current {@link Message.Builder} with the Firebase plateform-specified or common
   * information.
   *
   * @param messageBuilder current {@link Message.Builder}.
   */
  protected abstract void configure(Message.Builder messageBuilder);
}
