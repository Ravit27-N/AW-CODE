export const sumArrayByKey = ({array}: {array: any[]; key: any}) => {
  const sum = array
    .map(a => a.key)
    .reduce(function (a, b) {
      return a + b;
    });
  return sum;
};
