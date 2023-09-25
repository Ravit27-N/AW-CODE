package com.allweb.rms.core.scheduler.batch.task;

import com.allweb.rms.core.scheduler.batch.exception.TaskExecutionException;
import java.util.Map;
import java.util.Optional;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class AbstractTasklet implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    this.beforeExecute(contribution, chunkContext);
    this.validate();
    RepeatStatus repeatStatus = this.executeInternal(contribution, chunkContext);
    this.afterExecute(contribution, chunkContext);
    return repeatStatus;
  }

  protected abstract RepeatStatus executeInternal(
      StepContribution contribution, ChunkContext chunkContext) throws TaskExecutionException;

  protected abstract void validate();

  protected abstract void beforeExecute(StepContribution contribution, ChunkContext chunkContext);

  protected void afterExecute(StepContribution contribution, ChunkContext chunkContext) {}

  @SuppressWarnings("unchecked")
  protected <T> Optional<T> getJobParameter(String key, ChunkContext chunkContext) {
    Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
    if (parameters.containsKey(key)) {
      return Optional.of((T) parameters.get(key));
    }
    return Optional.empty();
  }

  protected ExecutionContext getJobExecutionContext(ChunkContext chunkContext) {
    return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
  }

  protected ExecutionContext getStepExecutionContext(ChunkContext chunkContext) {
    return chunkContext.getStepContext().getStepExecution().getExecutionContext();
  }
}
