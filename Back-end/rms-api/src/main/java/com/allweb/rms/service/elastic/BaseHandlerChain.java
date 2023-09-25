package com.allweb.rms.service.elastic;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseHandlerChain implements Handler {
  private final List<Handler> handlerList = new ArrayList<>();

  public void addHandler(Handler handler) {
    this.handlerList.add(handler);
  }

  @Override
  public void handle(ChainContext context) {
    Handler refHandler = null;
    try {
      for (Handler handler : this.handlerList) {
        refHandler = handler;
        handler.handle(context);
      }
    } catch (RuntimeException exception) {
      if (log.isDebugEnabled()) {
        log.debug(
            refHandler.getClass().getSimpleName() + " - " + exception.getMessage(), exception);
      } else {
        log.error(
            refHandler.getClass().getSimpleName() + " - " + exception.getMessage(), exception);
      }
    }
  }
}
