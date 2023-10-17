const randomFloat = function () {
  const int = window.crypto.getRandomValues(new Uint32Array(1))[0];
  return int / 2 ** 32;
};
const randomInt = function (min: number, max: number) {
  const range = max - min;
  return Math.floor(randomFloat() * range + min);
};
export const randomIntArray = function (
  length: number,
  min: number,
  max: number,
) {
  const result = new Array(length).fill(0).map(() => randomInt(min, max));
  return result[0];
};
