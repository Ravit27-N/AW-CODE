export const deepCompare = (arg1: any, arg2: any): boolean => {
  const keys1 = Object.keys(arg1);
  const keys2 = Object.keys(arg2);
  if (keys1.length !== keys2.length) {
    return false;
  }
  for (const key of keys1) {
    const val1 = arg1[key];
    const val2 = arg2[key];
    const areObjects = isObject(val1) && isObject(val2);
    const areObjectsNotEqual = areObjects && !deepCompare(val1, val2);
    const areNotObjectsNotEqual = !areObjects && val1 !== val2;
    if (areObjectsNotEqual || areNotObjectsNotEqual) {
      return false;
    }
  }
  return true;
};
export const isObject = (object: any): boolean => {
  return object != null && typeof object === 'object';
};
