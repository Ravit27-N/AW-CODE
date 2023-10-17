export const sumArrayByKey = ({array, key}: {array: any[]; key: any}) => {
  const sum = array
    .map(a => a.key)
    .reduce(function (a, b) {
      return a + b;
    });
  return sum;
};
