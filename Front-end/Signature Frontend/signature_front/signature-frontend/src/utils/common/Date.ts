import {Localization} from '@/i18n/lan';
import {Dayjs} from 'dayjs';
import {useTranslation} from 'react-i18next';
export const DateFrench = (D: Date) => {
  const {t} = useTranslation();
  const DayT = D.toString().slice(0, 3);
  const DATE = D.toString().split('G')[0];
  const time = DATE.split(' ')[4].slice(0, 5);
  const mon = DATE.split(' ')[1];
  const month = t(Localization('month', mon as any));
  const Day = t(Localization('day', DayT as any));
  const day = DATE.split(' ')[2];
  const year = DATE.split(' ')[3];
  const monthInNumber = D.getMonth() + 1;
  return {time, day, month, year, Day, mon, monthInNumber};
};

export const secondToDateFrench = (second: number) => {
  const d = new Date(second);
  return (
    DateFrench(d).day + ' ' + DateFrench(d).month + ' ' + DateFrench(d).year
  );
};

export const formatDate = (date: Dayjs) => {
  const day = date.get('D') < 10 ? `0${date.get('D')}` : date.get('D');
  const month =
    date.get('M') + 1 < 10 ? `0${date.get('M') + 1}` : date.get('M') + 1;

  return `${day}/${month}/${date.get('year')}`;
};

export const secondToDateFrenchWithTime = (second: number) => {
  const d = new Date(second);
  return (
    DateFrench(d).day +
    ' ' +
    DateFrench(d).month +
    ' ' +
    DateFrench(d).year +
    ' ' +
    d.toLocaleTimeString()
  );
};

// Convert second to human date like dd/mm/yyyy
export const secondToDate = (second: number) => {
  const d = new Date(second).toLocaleDateString('en-GB', {
    timeZone: 'UTC',
  });
  return d;
};
function secondsToHm(milisecond: number) {
  const second = milisecond / 1000;
  let h = Math.floor(second / 3600);
  const m = Math.floor((second % 3600) / 60);
  /** Temporary solution, we will update later, Agree from bong Vilay*/
  if (h > 23) {
    h = 23;
  }
  const hDisplay = h > 0 ? h + ' h ' : '0h';

  const mDisplay = m > 0 ? m + '  ' : '';

  return hDisplay + mDisplay;
}

export const getTimeFromSecondCompareToCurrentDate = (second: number) => {
  const now = Date.now();
  const period = now - second;

  return secondsToHm(period);
};

export const secondToFullDate = (second: number) => {
  const d = new Date(second);

  return (
    d.toDateString() +
    ' ' +
    d.toLocaleTimeString('en-US', {
      hourCycle: 'h23',
      timeZone: 'Europe/Paris',
    })
  );
};

export const FormatDateTime = (dateTime: Date) => {
  const date = new Date(dateTime).toLocaleDateString('en-US', {
    month: '2-digit',
    day: '2-digit',
    year: 'numeric',
  });
  const time = new Date(dateTime).toLocaleTimeString('en-US', {
    hour12: false,
  });

  return {date, time};
};

export const getTimeZoneOffSetStandard = (): string | undefined => {
  const timezone_offset_min = new Date().getTimezoneOffset();
  let offset_hrs: number | string = Math.abs(
    Math.ceil(timezone_offset_min / 60),
  );
  let offset_min: number | string = Math.abs(timezone_offset_min % 60);
  let timezone_standard: string | undefined = undefined;

  if (offset_hrs < 10) offset_hrs = '0' + offset_hrs;

  if (offset_min < 10) offset_min = '0' + offset_min;

  // Add an opposite sign to the offset
  // If offset is 0, it means timezone is UTC
  if (timezone_offset_min < 0)
    timezone_standard = '+' + offset_hrs + ':' + offset_min;
  else if (timezone_offset_min > 0)
    timezone_standard = '-' + offset_hrs + ':' + offset_min;
  else if (timezone_offset_min == 0) timezone_standard = 'Z';

  // Timezone difference in hours and minutes
  // String such as +5:30 or -6:00 or Z
  return timezone_standard;
};
export const dateFormatStepFour = (D: Date) => {
  return (
    new Date(D)
      .toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
      })
      .replace(/\. /g, '-')
      .replace(/\./g, '') +
    'T' +
    new Date(D).toLocaleTimeString('en-US', {
      hourCycle: 'h23',
      timeZone: 'Europe/Paris',
    }) +
    '.000Z'
  );
};
