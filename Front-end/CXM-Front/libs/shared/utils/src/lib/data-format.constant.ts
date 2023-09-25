export class DateFormatConstant {
  static readonly TIME_24_HOURS: string = 'HH:mm';
  static readonly ISO_8601: string = 'yyyy-MM-dd';
  static readonly DD_MM_yyyy: string = 'DD-MM-yyyy';
  static readonly yyyy_MM_dd: string = 'yyyy-MM-dd';
  static readonly DD_SLASH_MM_SLASH_YYYY: string = 'DD/MM/YYYY';
  static readonly dd_SLASH_MM_SLASH_yyyy: string = 'dd/MM/yyyy';
  static readonly yyyy_MM_DD: string = 'yyyy-MM-DD';

  static readonly DD_MM_yyyy_24_HOURS = `${DateFormatConstant.DD_MM_yyyy} ${DateFormatConstant.TIME_24_HOURS}`;
}
