package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.approve.ReadProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadProcessExecutionManager extends ExecutionManager {

    private final JsonFileProcessHandler jsonFileProcessHandler;

    private final ReadProcessHandler readProcessHandler;

    @Override
    public void afterPropertiesSet() {
        super.addHandlers(List.of(jsonFileProcessHandler, readProcessHandler, jsonFileProcessHandler));
    }
}
