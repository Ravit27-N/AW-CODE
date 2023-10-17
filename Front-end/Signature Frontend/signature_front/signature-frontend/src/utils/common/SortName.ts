export function shortName(name: string) {
  return `${name.split(' ')[0][0]}${name.split(' ')[1][0]}`;
}
export function shortNameCap(name: string) {
  return `${name.split(' ')[0][0].toUpperCase()}${name
    .split(' ')[1][0]
    .toUpperCase()}`;
}
