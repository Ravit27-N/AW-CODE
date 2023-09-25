import * as moment from 'moment/moment';

export * from './custom-material-ui.util';
export * from './base64.util';
export * from './file.util';
export * from './date.util';
export * from './asset-util';
export * from './blob-file';
export * from './dateCustomFormat';
export * from './datetime';
export * from './string.util';

export const firstDayWeeks = (d: Date) => {
  d = new Date(d);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
  return new Date(d.setDate(diff));
};

export const getMonthly = (d: Date) => {
  const start = moment(d);
  return {
    start: start.startOf('month').toDate(),
    end: start.endOf('month').toDate(),
  };
};

export const aWeekFrom = (d: Date) => {
  const diff = d.getDate() + 6;
  const date = new Date();
  date.setDate(diff);
  return date;
};

export const getAge = (date: Date | string): number =>
  moment().diff(moment(date), 'year');

export const dateInPast = (firstDate, secondDate) => {
  const diff = moment(firstDate, 'DD-MM-YYYY hh:mm').diff(
    moment(secondDate, 'DD-MM-YYYY hh:mm'),
  );
  return diff < 0;
};

export interface BlobResource {
  file: Blob;
  filename: string;
}

export const last7Days = (d: Date) => {
  const diff = d.getDate() - 6;
  const date = new Date();
  date.setDate(diff);
  return date;
};
