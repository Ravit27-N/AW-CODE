/**
 * use to get the previous number to limit the recursion as we proceed deeper into the object tree. Prev[10] is 9, and Prev[1] is 0.
 * */
export type Prev = [
  never,
  0,
  1,
  2,
  3,
  4,
  5,
  6,
  7,
  8,
  9,
  10,
  11,
  12,
  13,
  14,
  15,
  16,
  17,
  18,
  19,
  20,
  ...0[], // mean from this point forward all value are 0
];

/**
 * use to join two path together JoinPath<"a","b"> = "a.b"
 * */
export type JoinPath<K, P> = K extends string | number
  ? P extends string | number
    ? // prevent join "a","" = "a."
      `${K}${'' extends P ? '' : '.'}${P}`
    : never
  : never;

/**
 * use to get all nested object key. Ex: `{ a: string, b: { b1: number } }` = `"a" | "b" | "b.b1"`.
 * @param {object} T object type
 * @param {number} D depth of nest object that it get key from
 * */
export type GetKeys<T, D extends number = 10> = [D] extends [never]
  ? never
  : T extends object
  ? {
      [K in keyof T]-?: K extends string | number
        ? `${K}` | JoinPath<K, GetKeys<T[K], Prev[D]>>
        : never;
    }[keyof T]
  : '';
