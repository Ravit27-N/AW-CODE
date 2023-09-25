export const comparedDateNow = (date: Date) => {
  const now = new Date();
  return date.getTime() > now.getTime();
};

export const isDeadline = (date: number) => {
  const now = new Date();
  return date > now.getTime();
};

export const days = (date1: string | number | Date) => {
  const d = new Date(date1);
  const today = new Date();
  const dif = today.getTime() - d.getTime();
  return Math.floor(dif / (1000 * 3600 * 24));
};
