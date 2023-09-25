package com.tessi.cxm.pfl.ms8.core.flow.chain;

import com.tessi.cxm.pfl.ms8.core.flow.handler.ConvertPortalFileDocumentToGenericEmailEml;
import com.tessi.cxm.pfl.ms8.core.flow.handler.EmailBase64FileHandler;
import com.tessi.cxm.pfl.ms8.core.flow.handler.EmailToEmlFileConvertor;
import com.tessi.cxm.pfl.shared.core.chains.BaseExecutionHandlerChains;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignEmailEmlProcessingStep extends BaseExecutionHandlerChains
    implements InitializingBean {

  private final ConvertPortalFileDocumentToGenericEmailEml
      convertPortalFileDocumentToGenericEmailEml;
  private final EmailBase64FileHandler emailBase64FileHandler;
  private final EmailToEmlFileConvertor emailToEmlFileConvertor;
  /**
   * Invoked by the containing {@code BeanFactory} after it has set all bean properties and
   * satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
   *
   * <p>This method allows the bean instance to perform validation of its overall configuration and
   * final initialization when all bean properties have been set.
   *
   * @throws Exception in the event of misconfiguration (such as failure to set an essential
   *     property) or if initialization fails for any other reason
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    this.addExecutionHandler(convertPortalFileDocumentToGenericEmailEml);
    this.addExecutionHandler(emailBase64FileHandler);
    this.addExecutionHandler(emailToEmlFileConvertor);
  }
}
