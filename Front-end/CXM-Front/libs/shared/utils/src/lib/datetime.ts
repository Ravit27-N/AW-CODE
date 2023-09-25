import {
  API_DATE_ONLY_FORMAT,
  APP_DATE_ONLY_FORMAT,
  APP_DATETIME_FORMAT,
  APP_DATETIME_FORMAT_24,
  FULL_DATE_TIME_FORMAT,
  CALENDAR_FORMAT
} from '@cxm-smartflow/shared/data-access/model';
import * as moment from 'moment';
import { DatePipe } from '@angular/common';

const pipe = new DatePipe('en-US');

const formatDatetime = (date: Date | string) =>
  moment(date).format(APP_DATETIME_FORMAT);
const formatDateWithoutTime = (date: Date | string) =>
  moment(date).format(APP_DATE_ONLY_FORMAT);
const formatApiDateWithoutTime = (date: Date | string) =>
  moment(date).format(API_DATE_ONLY_FORMAT);

const calendarTime = (date: Date | string) =>
  moment(date).format(CALENDAR_FORMAT);
const formatDate24H = (date: Date | string) =>
  moment(date).format(APP_DATETIME_FORMAT_24);
const formatDatetimeFull = (date: Date | string) =>
  moment(date).format(APP_DATETIME_FORMAT_24);
const formatParse = (date: string, format: string) =>
  moment(date, format).toDate()

const formatToNewDate = (value: string | Date | number) => {
  return moment(value).toDate();
};

const formatDateTime = (value: string | Date | number, format: string) => moment(value).format(format);

export const formatDate = {
  formatDatetime,
  formatDateWithoutTime,
  formatApiDateWithoutTime,
  calendarTime,
  formatDate24H,
  formatDatetimeFull,
  formatToNewDate,
  formatParse,
  formatDateTime
};

export const getMonthly = () => {
  const currentDate = moment();
  return {
    start: getDatePipeFormat(currentDate.startOf('month').toLocaleString()),
    end: getDatePipeFormat(currentDate.endOf('month').toLocaleString())
  };
};

export const getDatePipeFormat = (date: Date | string) => pipe.transform(date, 'dd/MM/yyyy', 'short');
