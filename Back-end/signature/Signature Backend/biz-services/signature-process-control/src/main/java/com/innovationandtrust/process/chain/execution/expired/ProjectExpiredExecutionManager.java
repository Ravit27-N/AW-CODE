package com.innovationandtrust.process.chain.execution.expired;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredLauncherHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectExpiredExecutionManager extends ExecutionManager {

    private final JsonFileProcessHandler jsonFileProcessHandler;

    private final ProjectExpiredLauncherHandler updateProjectLauncherHandler;

    @Override
    public void afterPropertiesSet() {
        super.addHandlers(List.of(jsonFileProcessHandler, updateProjectLauncherHandler, jsonFileProcessHandler));
    }
}
