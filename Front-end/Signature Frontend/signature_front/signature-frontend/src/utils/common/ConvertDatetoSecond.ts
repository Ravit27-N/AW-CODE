export const convertUTCToLocalTime = (dateString: any) => {
  const date = new Date(dateString);
  const milliseconds = Date.UTC(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
    date.getHours(),
    date.getMinutes(),
    date.getSeconds(),
  );
  return new Date(milliseconds);
};
export const convertUTCToLocalTime_second = (dateString: any) => {
  const date = new Date(dateString);
  const milliseconds = Date.UTC(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
    date.getHours(),
    date.getMinutes(),
    date.getSeconds(),
  );
  return milliseconds / 1000;
};
export const convertUTCToLocalTimeCN = (
  date: number | Date,
  format = 'zh-Hans-CN',
): string => {
  return new Date(date)
    .toLocaleDateString(format, {
      month: '2-digit',
      day: '2-digit',
      year: 'numeric',
    })
    .replace(/\//g, '-');
};
