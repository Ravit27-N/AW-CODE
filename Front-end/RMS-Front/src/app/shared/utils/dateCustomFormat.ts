import { DatePipe } from '@angular/common';
import * as moment from 'moment';
const pipe = new DatePipe('en-US');

export const getToday = () => pipe.transform(new Date(), 'dd-MM-yyyy', 'short');

export const getWeekly = () => {
  const currentDate = moment();
  const start = currentDate.startOf('isoWeek').toLocaleString();
  const end = currentDate.endOf('isoWeek').toLocaleString();
  return {
    start: getDatePipeFormat(start),
    end: getDatePipeFormat(end)
  };
};

export const getMonthly = () => {
  const currentDate = moment();
  const start = currentDate.startOf('month').toLocaleString();
  const end = currentDate.endOf('month').toLocaleString();
  return {
    s: getDatePipeFormat(start),
    e: getDatePipeFormat(end)
  };
};

export const getDatePipeFormat = (date: Date | string) => pipe.transform(date, 'dd-MM-yyyy', 'short');
