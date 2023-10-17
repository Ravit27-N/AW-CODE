import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class ICaTokenTypeMap implements BaseTypeMap {
    aggregate?: Prisma.CaTokenAggregateArgs;
    count?: Prisma.CaTokenCountArgs;
    create?: Prisma.CaTokenCreateArgs;
    delete?: Prisma.CaTokenDeleteArgs;
    deleteMany?: Prisma.CaTokenDeleteManyArgs;
    findFirst?: Prisma.CaTokenFindFirstArgs;
    findMany?: Prisma.CaTokenFindManyArgs;
    findUnique?: Prisma.CaTokenFindUniqueArgs;
    update?: Prisma.CaTokenUpdateArgs;
    updateMany?: Prisma.CaTokenUpdateManyArgs;
    upsert?: Prisma.CaTokenUpsertArgs;
}
