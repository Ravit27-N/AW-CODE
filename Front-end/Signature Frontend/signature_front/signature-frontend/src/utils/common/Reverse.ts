export const ArrayReverse = <T>(array: T[]): T[] => {
  const newArr: T[] = [],
    inArr = array.slice(); // take copy of primitive values

  while (inArr.length) {
    // check decrementing length
    newArr.push(inArr.pop()!);
  }
  return newArr;
};
