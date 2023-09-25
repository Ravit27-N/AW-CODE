package com.tessi.cxm.pfl.ms3.constant;

import com.tessi.cxm.pfl.shared.core.concurrent.ReentrantLockSynchronizer;

public class FlowBatchConcurrentLockingUtils {
  private static final ReentrantLockSynchronizer<Long> SYNCHRONIZER =
      new ReentrantLockSynchronizer<>();

  private FlowBatchConcurrentLockingUtils() {}

  /**
   * Create a new concurrent synchronizer for a specific key.
   *
   * @param key Key use to maintain synchronization of concurrent execution.
   */
  public static void acquireSyn(Long key) {
    SYNCHRONIZER.sync(key);
  }

  /**
   * Remove a concurrent synchronizer by a specific key.
   *
   * @param key Key of associated concurrent synchronizer.
   */
  public static void releaseSync(Long key) {
    SYNCHRONIZER.unSync(key);
  }

  /**
   * @param key Key of associated concurrent synchronizer which is used to maintains
   *     synchronization.
   * @param runnable Task to be executed in synchronization.
   */
  public static void executeSynchronized(Long key, Runnable runnable) {
    SYNCHRONIZER.executeSyn(key, runnable);
  }
}
