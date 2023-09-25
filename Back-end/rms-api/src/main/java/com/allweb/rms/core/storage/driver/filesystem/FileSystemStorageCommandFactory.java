package com.allweb.rms.core.storage.driver.filesystem;

import com.allweb.rms.core.storage.commands.StorageCommand;
import com.allweb.rms.core.storage.commands.StorageCommandFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemStorageCommandFactory implements StorageCommandFactory {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(FileSystemStorageCommandFactory.class);
  private static final ExecutorService REFLECTION_SCANNER_EXECUTORSERVICE =
      Executors.newCachedThreadPool();

  private final Map<String, StorageCommand> storageCommandObjectCaches = new ConcurrentHashMap<>();

  public FileSystemStorageCommandFactory() {
    Set<Class<? extends StorageCommand>> storageCommandClazzes = scanStorageCommandClasses();
    if (storageCommandClazzes != null && !storageCommandClazzes.isEmpty()) {
      for (Class<? extends StorageCommand> storageCommandClazz : storageCommandClazzes) {
        Map.Entry<String, StorageCommand> storageCommandInstanceMapEntry;
        try {
          storageCommandInstanceMapEntry =
              getStorageCommandInstanceMapEntryFromClass(storageCommandClazz);
          if (storageCommandInstanceMapEntry != null) {
            storageCommandObjectCaches.put(
                storageCommandInstanceMapEntry.getKey(), storageCommandInstanceMapEntry.getValue());
          }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
          LOGGER.debug(e.getMessage(), e);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param command Command text represent a command object.
   * @return {@link StorageCommand} object.
   */
  @Override
  public StorageCommand getCommand(String command) {
    String storageCommandClassName = matchStorageCommandClassName(command);
    return storageCommandObjectCaches.get(storageCommandClassName);
  }

  String matchStorageCommandClassName(String commandText) {
    if (StringUtils.isNotBlank(commandText)) {
      return StringUtils.capitalize(commandText) + "Command";
    }
    return commandText;
  }

  Set<Class<? extends StorageCommand>> scanStorageCommandClasses() {
    ConfigurationBuilder reflectionConfigBuilder =
        new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forClass(StorageCommand.class))
            .setScanners(new SubTypesScanner());
    //                .setExecutorService(REFLECTION_SCANNER_EXECUTORSERVICE);
    Reflections reflections = new Reflections(reflectionConfigBuilder);
    return reflections.getSubTypesOf(StorageCommand.class).stream()
        .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
        .collect(Collectors.toSet());
  }

  Map.Entry<String, StorageCommand> getStorageCommandInstanceMapEntryFromClass(
      Class<? extends StorageCommand> clazz)
      throws InstantiationException, IllegalAccessException, InvocationTargetException {
    String commandSuffix = "Command";
    String commandNameWithoutSuffix = clazz.getSimpleName().replace(commandSuffix, "");
    commandNameWithoutSuffix =
        commandNameWithoutSuffix.substring(0, 1).toUpperCase()
            + commandNameWithoutSuffix.substring(1).toLowerCase();
    String clazzName = String.format("%s%s", commandNameWithoutSuffix, commandSuffix);
    Constructor<?> defaultConstructor = clazz.getConstructors()[0];
    return new AbstractMap.SimpleEntry<>(
        clazzName, (StorageCommand) defaultConstructor.newInstance());
  }
}
