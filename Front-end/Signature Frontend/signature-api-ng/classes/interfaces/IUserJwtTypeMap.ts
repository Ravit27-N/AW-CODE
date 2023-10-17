import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class IUserJwtTypeMap implements BaseTypeMap {
    aggregate?: Prisma.UserJwtAggregateArgs;
    count?: Prisma.UserJwtCountArgs;
    create?: Prisma.UserJwtCreateArgs;
    delete?: Prisma.UserJwtDeleteArgs;
    deleteMany?: Prisma.UserJwtDeleteManyArgs;
    findFirst?: Prisma.UserJwtFindFirstArgs;
    findMany?: Prisma.UserJwtFindManyArgs;
    findUnique?: Prisma.UserJwtFindUniqueArgs;
    update?: Prisma.UserJwtUpdateArgs;
    updateMany?: Prisma.UserJwtUpdateManyArgs;
    upsert?: Prisma.UserJwtUpsertArgs;
}
