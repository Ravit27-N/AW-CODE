export class TimeZoneUtil {

  static getTimeZone(): string {
    return Intl.DateTimeFormat().resolvedOptions().timeZone;
  }

}
