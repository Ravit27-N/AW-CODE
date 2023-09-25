package com.allweb.rms.core.storage.commands;

public interface StorageCommandFactory {

  /**
   * Get a {@link StorageCommand} object match to the {@code command} argument.
   *
   * @param command Command text represent a command object.
   * @return {@link StorageCommand} object.
   */
  StorageCommand getCommand(String command);
}
