export class URLUtils {

  public static getQueryParamByKey(key: string): string | null {
    const url = new URL(window.location.href);
    const queryParams = new URLSearchParams(url.search);
    return queryParams.get(key);
  }
}
