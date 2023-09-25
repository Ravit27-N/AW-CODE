package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.ServiceUtils;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationRecordHandler extends AbstractExecutionHandler implements ServiceUtils {

  private final Validator validator;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    UserRecord userRecord = context.get(DataReaderConstant.USER_RECORD, UserRecord.class);
    this.validate(userRecord);
    return ExecutionState.NEXT;
  }

  @Override
  public Validator getValidator() {
    return validator;
  }
}
