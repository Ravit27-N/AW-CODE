export type Opaque<V> = V & {
  readonly __opq__: unique symbol;
};

export type int32 = Opaque<number>;
export type int = Opaque<number>;
export type uint32 = Opaque<number>;
export type uint = Opaque<number>;

export declare const INT32_MIN: int32;
export declare const INT_MAX: int;
export declare const INT_MIN: int;
export declare const UINT32_MAX: uint32;
export declare const UINT_MAX: uint;
export declare const UINT_MIN: uint;

export type Dictionary<T = any> = {
  [k: string]: T;
};
export type AnyDictionary = Dictionary;

export function $ok(o: any): boolean {
  return o !== undefined && typeof o !== 'undefined' && o !== null;
}

export type Nullable<V> = V | null | undefined;

export function $toUnsigned(v: Nullable<string | number>, defaultValue?: uint) {
  if (!$ok(v)) {
    return defaultValue;
  }
  if (typeof v === 'string') {
    v = parseInt(v, 10);
  }
  return isNaN(v!)
    ? defaultValue
    : Math.max(UINT_MIN, $iCast(Math.min(v!, UINT_MAX)));
}

export function $iCast(v: number): number {
  if (v >= 0) {
    return v <= UINT32_MAX ? v | 0 : Math.floor(v);
  } else {
    return v >= INT32_MIN ? -(-v | 0) : -Math.floor(-v);
  }
}

export function $isstring(o: any): boolean {
  return typeof o === 'string';
}
export function $isarray(o: any): boolean {
  return Array.isArray(o);
}

export function $isNumber(o: any): boolean {
  return $ok(o) && typeof o === 'number' && !isNaN(o) && isFinite(o);
}

export function $count<T = any>(a: Nullable<ArrayLike<T>>) {
  return $ok(a) ? a?.length : 0;
}

export enum NGUserRole {
  Request = 1,
  Action = 2,
  Maintenance = 3,
  System = 4,
}
