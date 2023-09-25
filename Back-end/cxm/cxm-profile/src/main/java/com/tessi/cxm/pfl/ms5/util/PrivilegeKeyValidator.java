package com.tessi.cxm.pfl.ms5.util;

import com.tessi.cxm.pfl.ms5.constant.PrivilegeLevelValidator;
import com.tessi.cxm.pfl.ms5.exception.PrivilegeKeyNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrivilegeKeyValidator {

  private static final Map<String, Function<String, Boolean>> DELEGATE_KEY_EXISTING_VALIDATOR =
      new HashMap<>();
  private static final Map<String, Function<String, PrivilegeLevelValidator>>
      DELEGATE_LEVEL_VALIDATOR = new HashMap<>();

  private PrivilegeKeyValidator() {
  }

  public static void registerKeyExistingValidator(
      String key, Function<String, Boolean> keyExistValidatorFunc) {
    DELEGATE_KEY_EXISTING_VALIDATOR.merge(
        key,
        keyExistValidatorFunc,
        (k, v) -> {
          throw new IllegalArgumentException(
              "Privilege validator key is duplicated: '" + key + "'.");
        });
  }

  public static boolean keyExists(String key) {
    return DELEGATE_KEY_EXISTING_VALIDATOR.values().stream()
        .anyMatch(keyExistValidator -> keyExistValidator.apply(key));
  }

  public static void registerLevelValidator(
      String key, Function<String, PrivilegeLevelValidator> keyExistValidatorFunc) {
    DELEGATE_LEVEL_VALIDATOR.merge(
        key,
        keyExistValidatorFunc,
        (k, v) -> {
          throw new IllegalArgumentException(
              "Privilege validator key is duplicated: '" + key + "'.");
        });
  }

  public static PrivilegeLevelValidator getPrivilegeLevel(String key) {
    return DELEGATE_LEVEL_VALIDATOR.values().stream()
        .filter(
            privilegeEntry -> {
              try {
                privilegeEntry.apply(key);
                return true;
              } catch (Exception ex) {
                log.debug(ex.getMessage(), ex);
                return false;
              }
            })
        .map(keyExistValidator -> keyExistValidator.apply(key))
        .findFirst()
        .orElseThrow(() -> new PrivilegeKeyNotFoundException(key));
  }
}
