package com.innovationandtrust.utils.chain;

import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public abstract class ExecutionManager implements InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(ExecutionManager.class);
  private final List<AbstractExecutionHandler> handlers = new ArrayList<>();

  protected void addHandlers(List<AbstractExecutionHandler> executionHandlers) {
    this.handlers.addAll(executionHandlers);
  }

  protected void addHandler(AbstractExecutionHandler handler) {
    this.handlers.add(handler);
  }

  public void execute(ExecutionContext context) {
    Iterator<AbstractExecutionHandler> var2 = this.handlers.iterator();

    ExecutionState state;
    do {
      if (!var2.hasNext()) {
        return;
      }

      AbstractExecutionHandler handler = var2.next();
      log.info("The {} {}", handler.getClass().getName(), " is executing");
      state = handler.execute(context);
      if (ExecutionState.SKIP_NEXT.equals(state)) {
        var clazz = var2.next();
        log.info("Skip execution of {}", clazz.getClass().getName());
      }
    } while (ExecutionState.NEXT.equals(state) || ExecutionState.SKIP_NEXT.equals(state));
  }

  @Override
  public void afterPropertiesSet() {
    // do nothing
  }
}
