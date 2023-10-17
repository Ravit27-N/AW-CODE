import { Prisma } from "@prisma/client";

export type Enumerable<T> = T | Array<T>;
export interface IWhereInput {
    AND?: Enumerable<IWhereInput>;
    OR?: Enumerable<IWhereInput>;
    NOT?: Enumerable<IWhereInput>;
    ttl?: Prisma.BigIntFilter | number;
    user?: Prisma.StringFilter | string;
    expiredAt?: Prisma.DateTimeFilter | Date;
}

export interface APIGetListQuery {
    ttlmin?: number;
    ttlmax?: number;
    dynttlmin?: number;
    dynttlmax?: number;
    userids?: string[] | string;
    expirationstatus?: "all" | "expired" | "valid";
}
