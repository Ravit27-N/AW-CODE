import { NgxMatMomentAdapter, NgxMatMomentDateAdapterOptions } from '@angular-material-components/moment-adapter';
import { APP_DATETIME_FORMAT } from '../core';
import * as moment from 'moment';

export const APP_DATE_FORMATS =
{
  parse: {
    // dateInput: 'l, LTS'
    dateInput: APP_DATETIME_FORMAT
  },
  display: {
    dateInput: 'DD / MM / YYYY hh:mm A',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY'
  }
};

export class CustomDatetimeFormat extends NgxMatMomentAdapter {

  constructor(dateLocale: string, options?: NgxMatMomentDateAdapterOptions) {
    super(dateLocale, options);
  }

  deserialize(value: any): moment.Moment | null {
    return super.deserialize(moment(value, APP_DATE_FORMATS.parse.dateInput, true).toDate());
  }
}

