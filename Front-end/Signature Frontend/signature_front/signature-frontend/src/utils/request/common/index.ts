export function $timeOut(
  promise: Promise<any>,
  time: number,
  exception: any,
): Promise<any> {
  let timer: any;
  return Promise.race([
    promise,
    new Promise(
      (_, rejection) => (timer = setTimeout(rejection, time, exception)),
    ),
  ]).finally(() => clearTimeout(timer));
}
