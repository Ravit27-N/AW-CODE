import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class INgConfigTypeMap implements BaseTypeMap {
    aggregate?: Prisma.NgConfAggregateArgs;
    count?: Prisma.NgConfCountArgs;
    create?: Prisma.NgConfCreateArgs;
    delete?: Prisma.NgConfDeleteArgs;
    deleteMany?: Prisma.NgConfDeleteManyArgs;
    findFirst?: Prisma.NgConfFindFirstArgs;
    findMany?: Prisma.NgConfFindManyArgs;
    findUnique?: Prisma.NgConfFindUniqueArgs;
    update?: Prisma.NgConfUpdateArgs;
    updateMany?: Prisma.NgConfUpdateManyArgs;
    upsert?: Prisma.NgConfUpsertArgs;
}
