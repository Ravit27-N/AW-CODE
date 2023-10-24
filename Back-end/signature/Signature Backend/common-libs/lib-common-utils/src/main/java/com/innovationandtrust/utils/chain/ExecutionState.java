package com.innovationandtrust.utils.chain;

/**
 * Represent the status of the executed task.
 *
 * <p>{@code NEXT} to state that the current executed task is finished successfully and should be
 * proceeded to the next task.
 *
 * <p>{@code END} to state that the current executed task is finished successfully but should not *
 *  proceed to the next task.
 */
public enum ExecutionState {

  NEXT,
  SKIP_NEXT,
  END
}