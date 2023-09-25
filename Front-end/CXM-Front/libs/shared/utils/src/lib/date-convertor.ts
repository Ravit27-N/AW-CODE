import * as moment from 'moment';
import { DatePipe } from '@angular/common';
import { DateFormatConstant } from './data-format.constant';

export const formatDateRequest = DateFormatConstant.yyyy_MM_dd;
const pipe = new DatePipe('en-US');

/**
 * convert datetime to date
 *
 * @param datetime
 * @returns string of the date (format: dd/mm/yyyy)
 */
export function convertDateTimeToDate(datetime: string | number) {
  return moment(datetime).format(DateFormatConstant.DD_SLASH_MM_SLASH_YYYY);
  // const date = new Date(datetime);
  // const mnth = ('0' + (date.getMonth() + 1)).slice(-2);
  // const day = ('0' + date.getDate()).slice(-2);
  // return [day, mnth, date.getFullYear()].join('/');
}

/**
 * convert  datetime to hours
 * @param datetime
 * @returns string of hours (format: hh:mm)
 */
export function convertDateTimeToTime(datetime: string | number) {
  return moment(datetime).format(DateFormatConstant.TIME_24_HOURS);
  // const date = new Date(datetime);
  // const hours = ('0' + date.getHours()).slice(-2);
  // const minutes = ('0' + date.getMinutes()).slice(-2);
  // return [hours, minutes].join(':');
}

export const dateConvertor = {
  convertDateTimeToTime,
  convertDateTimeToDate,
};

/**
 * Method used to convert normal date to 'DD-MM-yyyy HH:mm'.
 * @param date
 */
export const convertDateTime = (date: Date | string | undefined | null) => {
  return resolveAsMomentJSDate(date).format(
    DateFormatConstant.DD_MM_yyyy_24_HOURS
  );
};

/**
 * Method used to convert normal date to 'DD-MM-yyyy'.
 * @param date
 * @returns
 */
export const convertDate = (date: Date | string | undefined | null) => {
  return resolveAsMomentJSDate(date).format(DateFormatConstant.DD_MM_yyyy);
};

/**
 * Method ued to convert normal date to 'yyyy-MM-dd'.
 * @param date
 * @returns
 */
export const dateConvert = (date: Date | string | undefined | null) => {
  return resolveAsMomentJSDate(date).format(DateFormatConstant.yyyy_MM_DD);
};

/**
 * Method used to convert normal date to 'DD-MM-yyyy HH:mm'.
 * @param date
 */
export const convertDateTimeToTimeStamp = (
  date: Date | string | undefined | null
) => resolveAsMomentJSDate(date).format('x');

const resolveAsMomentJSDate = (date: Date | string | undefined | null) =>
  moment(new Date(date ? date : new Date()));

export const formatDateToRequest = (date: Date | string) =>
  pipe.transform(date, formatDateRequest, 'short');
