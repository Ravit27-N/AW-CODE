/**
 * @author Chamrong THOR
 * @since 12/2/2022
 */
export class FalsyUtil {
  /**
   * Check if {@param isTruthy} is truthy return {@param isTruthy}
   * else return {@param falsyValue}
   *
   * @param isTruthy
   * @param falsyValue
   */
  static isTruthyReturnValue<T, FV>(isTruthy: T, falsyValue: FV): T | FV {
    return isTruthy ? isTruthy : falsyValue;
  }

  /**
   * Check the object is empty.
   *
   * Return true if object is empty else return false.
   * @param object
   */
  static isEmptyObject<T>(object: T): boolean {
    return Object.keys(object).length === 0;
  }
}
