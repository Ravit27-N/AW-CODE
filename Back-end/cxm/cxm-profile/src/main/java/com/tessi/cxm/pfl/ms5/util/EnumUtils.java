package com.tessi.cxm.pfl.ms5.util;

import com.tessi.cxm.pfl.ms5.constant.BaseEnumPrivilege;
import com.tessi.cxm.pfl.ms5.constant.PrivilegeLevelValidator;
import com.tessi.cxm.pfl.ms5.constant.ProfileConstants;
import com.tessi.cxm.pfl.ms5.exception.PrivilegeKeyNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * EnumUtils is used to get functionality of {@link Enum}.
 *
 * @author Sokhour LACH
 * @since 31/12/2021
 */
public class EnumUtils {
  private EnumUtils() {}

  /**
   * To retrieve privilege level.
   *
   * @param enumClass refer to enum class that extends from {@link Enum} and {@link
   *     BaseEnumPrivilege}
   * @param key key of the enum constant.
   * @param <E> refer to enum class that extends from {@link Enum} and {@link BaseEnumPrivilege}.
   * @return object of {@link PrivilegeLevelValidator}
   */
  public static <E extends Enum<E> & BaseEnumPrivilege> PrivilegeLevelValidator getPrivilegeLevel(
      Class<E> enumClass, String key) {

    return Arrays.stream(enumClass.getEnumConstants())
        .filter(v -> v.getKey().equalsIgnoreCase(key))
        .map(
            v -> {
              if (v.isVisibility()) {
                return PrivilegeLevelValidator.VISIBILITY;
              }
              if (v.isModification()) {
                return PrivilegeLevelValidator.MODIFICATION;
              }
              return PrivilegeLevelValidator.OWNER;
            })
        .findFirst()
        .orElseThrow(() -> new PrivilegeKeyNotFoundException(key));
  }

  /**
   * To get key value of enumeration.
   *
   * @param enumClass refer to enum class that extends from {@link Enum} and {@link
   *     BaseEnumPrivilege}
   * @param <E> refer to enum class that extends from {@link Enum} and {@link BaseEnumPrivilege}.
   * @return return list of keys {@link String} and values {@link String} wrapped by {@link Map}
   */
  public static <E extends Enum<E> & BaseEnumPrivilege> List<Map<String, Object>> getKeyValue(
      Class<E> enumClass) {
    List<Map<String, Object>> keyValue = new ArrayList<>();
    Arrays.stream(enumClass.getEnumConstants())
        .forEach(
            v ->
                keyValue.add(
                    Map.of(
                        ProfileConstants.OBJECT_KEY,
                        v.getKey(),
                        ProfileConstants.OBJECT_VALUE,
                        v.getValue(),
                        ProfileConstants.IS_VISIBILITY,
                        v.isVisibility(),
                        ProfileConstants.IS_MODIFICATION,
                        v.isModification())));
    return keyValue;
  }

  /**
   * To check key into enum class.
   *
   * @param enumClass refer to enum class that extends from {@link Enum} and {@link
   *     BaseEnumPrivilege}
   * @param key key of the enum constant.
   * @param <E> refer to enum class that extends from {@link Enum} and {@link BaseEnumPrivilege}.
   * @return true if key exists
   */
  public static <E extends Enum<E> & BaseEnumPrivilege> boolean keyExists(
      Class<E> enumClass, String key) {
    return Arrays.stream(enumClass.getEnumConstants())
        .anyMatch(privilege -> privilege.getKey().equalsIgnoreCase(key));
  }

  /**
   * To get object of enumeration that filter by key of enum.
   *
   * @param enumClass refer to enum class that extends from {@link Enum} and {@link
   *     BaseEnumPrivilege}
   * @param key key of the enum constant.
   * @param <E> refer to enum class that extends from {@link Enum} and {@link BaseEnumPrivilege}.
   * @return return type {@link Optional} of {@link E}
   */
  public static <E extends Enum<E> & BaseEnumPrivilege> Optional<E> valueByKey(
      Class<E> enumClass, String key) {
    return Arrays.stream(enumClass.getEnumConstants())
        .filter(v -> v.getKey().equalsIgnoreCase(key))
        .findFirst();
  }
}
