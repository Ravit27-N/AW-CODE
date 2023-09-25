import {DateFormatter} from "@cxm-smartflow/analytics/util";

export class DateRequest {

  static getRequestedAt(): string {
    const date = new Date();
    const formatter = new DateFormatter();
    const requestedAt = formatter
      .setYear(date.getFullYear())
      .setMonth(date.getMonth() + 1)
      .setDay(date.getDate())
      .setHours(date.getHours())
      .setMinutes(date.getMinutes())
      .setSeconds(date.getSeconds())
      .formatDate();
    return requestedAt;
  }


}
