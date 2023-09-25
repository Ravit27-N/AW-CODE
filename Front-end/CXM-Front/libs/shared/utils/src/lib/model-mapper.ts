export const removeFalsyObject = (object: any) => {
  return convertArrayToObject(Object.entries(object)?.filter(e => e[1]));
};

export const convertArrayToObject = (array: any[]) => {
  const map = new Map();
  array.forEach(e => map.set(e[0], e[1]));
  return Array.from(map).reduce((obj, [key, value]) => (Object.assign(obj, { [key]: value })), {});
};
