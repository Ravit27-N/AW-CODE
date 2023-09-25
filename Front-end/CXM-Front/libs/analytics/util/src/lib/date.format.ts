export class DateFormatter {
  private _year = '';
  private _month = '';
  private _day = '';
  private _hours = '';
  private _minutes = '';
  private _seconds = '';

  constructor() {}

  public setYear(year: number): this {
    this._year = year.toString();
    return this;
  }

  public setMonth(month: number): this {
    this._month = month.toString().padStart(2, '0');
    return this;
  }

  public setDay(day: number): this {
    this._day = day.toString().padStart(2, '0');
    return this;
  }

  public setHours(hours: number): this {
    this._hours = hours.toString().padStart(2, '0');
    return this;
  }

  public setMinutes(minutes: number): this {
    this._minutes = minutes.toString().padStart(2, '0');
    return this;
  }

  public setSeconds(seconds: number): this {
    this._seconds = seconds.toString().padStart(2, '0');
    return this;
  }

  public setDate(date: Date): this {
    date = new Date(date);
    this._year = `${date.getFullYear()}`;
    this._month = `${date.getMonth() + 1}`.padStart(2, '0');
    this._day = `${date.getDate()}`.padStart(2, '0');
    this._hours = `${date.getHours()}`.padStart(2, '0');
    this._minutes = `${date.getMinutes()}`.padStart(2, '0');
    this._seconds = `${date.getSeconds()}`.padStart(2, '0');

    return this;
  }

  public formatDate(): string {
    return `${this._year}-${this._month}-${this._day} ${this._hours}:${this._minutes}:${this._seconds}`;
  }

  public formatToYYYYMMdd(): string {
    return `${this._year}-${this._month}-${this._day}`;
  }
}
