import { DatePipe } from '@angular/common';

export const formatDateRequest = 'yyyy-MM-dd';

const pipe = new DatePipe('en-US');

export interface DateRangeModel {
  startDate: Date | string;
  endDate: Date | string;
}

export function getDateRangeLast7Days(isHasFormat = false): DateRangeModel {
  const currentDate = new Date();
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - 6);

  if (isHasFormat) {
    return {
      startDate: formatDateToRequest(startDate.toDateString()) || '',
      endDate: formatDateToRequest(currentDate.toDateString()) || '',
    };
  }
  return { startDate, endDate: currentDate };
}

// export function formatDateToRequest(date: string): string {
//   return moment(date).format(formatDateRequest);
// }
export const formatDateToRequest = (date: Date | string) =>
  pipe.transform(date, formatDateRequest, 'short');

export declare type DateRangeType =
  | 'flowTraceability'
  | 'flowDocument'
  | 'viewDocumentShipment';
