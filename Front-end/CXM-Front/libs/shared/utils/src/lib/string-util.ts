export class StringUtil {

  /**
   * Replace all text in string by text.
   * @param content - content of string.
   * @param search - text for search.
   * @param replace - text for replace.
   */
  public static replaceAll = (content: string, search: string, replace: string): string => {
    return content?.split(search).join(replace);
  }

  /**
   * Replace all text in string by regex.
   * @param content - content of string.
   * @param regex - value of {RegExp}.
   * @param replace - text for replace.
   */
  public static replaceAllByRegex =  (content: string, regex: RegExp, replace: string): string => {
    return content?.replace(regex, replace);
  }
}
