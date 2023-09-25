import { APP_DATETIME_FORMAT_24 } from '../../core/constant';
import * as moment from 'moment';
import {
  APP_DATETIME_FORMAT,
  APP_DATE_ONLY_FORMAT,
  API_DATE_ONLY_FORMAT,
  CALENDAR_FORMAT,
} from '../../core';

export const formatDate = (date: Date | string) =>
  moment(date).format(APP_DATETIME_FORMAT);
export const formatDateWithoutTime = (date: Date | string) =>
  moment(date).format(APP_DATE_ONLY_FORMAT);
export const formatApiDateWithoutTime = (date: Date | string) =>
  moment(date).format(API_DATE_ONLY_FORMAT);

export const calendarTime = (date: Date | string) =>
  moment(date).format(CALENDAR_FORMAT);
export const formatDate24H = (date: Date | string) =>
  moment(date).format(APP_DATETIME_FORMAT_24);
