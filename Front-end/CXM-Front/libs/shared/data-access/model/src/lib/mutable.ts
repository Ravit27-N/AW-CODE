export type Mutable<Type> = {
  -readonly [Value in keyof Type]: Type[Value];
}
