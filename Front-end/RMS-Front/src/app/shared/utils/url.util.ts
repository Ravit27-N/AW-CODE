export class UrlUtil {
  private static getQueryParam(key: string, params: URLSearchParams): string | null {
    return params.get(key);
  }

  static getParamsByKey(key: string) {
    const url = new URL(location.href);
    const searchParams = url.searchParams;
    return this.getQueryParam(key, searchParams);
  }
}
